//this interacts with the Database.db file
// all catch exceptions are logged using the logger instead of printing stack traces

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data access layer for the application's SQLite database
 * ({@code Database/Database.db}).
 * <p>
 * This is a static class that talks to the backend, objects of it cannot be
 * created.
 * It provides methods for creating the database, managing users (creating them
 * and logging in), items creation and cards, and for authenticating users.
 * Every query uses
 * {@link PreparedStatement} parameters to prevent SQL injection, and all
 * {@link SQLException}s are logged through a {@link Logger} rather than
 * being printed or rethrown, so methods signal failure through their return
 * values (see each method's documentation).
 * </p>
 *
 * <p>
 * The database contains four tables:
 * </p>
 * <ul>
 * <li>{@code users} - registered accounts</li>
 * <li>{@code items} - products available for purchase</li>
 * <li>{@code orders} - items ordered by users</li>
 * <li>{@code cards} - payment cards on file, deleted automatically when
 * the owning user is deleted</li>
 * </ul>
 */
public final class Controller {

    /** Prevents instantiation, since this class only contains static methods. */
    private Controller() {
    }

    /** JDBC connection URL for the SQLite database file. */
    private static final String URL = "jdbc:sqlite:Database/Database.db";

    /** Logger used to record all database and file errors. */
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    private static Connection connect() throws SQLException {

        new java.io.File("Database").mkdirs(); // make the database foler if it doesn't exist

        Connection conn = DriverManager.getConnection(URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * Creates the {@code users}, {@code items}, {@code orders} and
     * {@code cards} tables if they do not already exist.
     * <p>
     * Intended to be called once when the application is run. Also prints the
     * absolute path of the database file and whether the {@code Database}
     * directory exists, which is used for debugging purposes. If table creation
     * fails, the error is logged at {@link Level#SEVERE} and the method
     * returns normally.
     * </p>
     */
    public static void createTables() {
        System.out.println(new java.io.File("Database/Database.db").getAbsolutePath());
        System.out.println(new java.io.File("Database").exists());

        String users = """
                CREATE TABLE IF NOT EXISTS users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                username TEXT NOT NULL UNIQUE,
                address TEXT NOT NULL,
                password TEXT NOT NULL
                )""";

        String items = """
                CREATE TABLE IF NOT EXISTS items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                price REAL NOT NULL,
                stock INTEGER NOT NULL DEFAULT 0
                )""";

        String orders = """
                CREATE TABLE IF NOT EXISTS orders(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                item_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (item_id) REFERENCES items(id)
                )""";

        String cards = """
                CREATE TABLE IF NOT EXISTS cards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                card_holder TEXT NOT NULL,
                last_four TEXT NOT NULL,
                expiry TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
                """; // cards are deleted if a users account is deleted

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(items);
            stmt.execute(orders);
            stmt.execute(cards);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize database tables.", e);
        }
    }

    /**
     * Adds a new user to the database.
     * <p>
     * The user's name, username and address are read from the given
     * {@link User}; the password is supplied separately so it is not stored
     * on the {@code User} object. Insertion fails if the username is already
     * taken (usernames are unique).
     * </p>
     *
     * @param u        the user whose name, username and address will be stored
     * @param password the password to store for the user
     * @return {@code true} if the user was inserted; {@code false} if the
     *         insert failed (for example, a duplicate username or a database
     *         error)
     */
    public static boolean addUser(User u, String password) {
        String name = u.getName();
        String username = u.getUsername();
        String address = u.getAddress();

        String sql = "INSERT INTO users (name, username, address, password) VALUES (?,?,?,?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) // adding the user to the
                                                                                             // database
        {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, address);
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) // couldn't add user for some reason
        {
            logger.log(Level.SEVERE, "Error inserting new user into database.", e);
            return false;
        }
    }

    /**
     * Checks whether a username is already used in the database.
     *
     * @param u the username to look up
     * @return {@code true} if a user with that username exists; {@code false}
     *         if it does not, or if a database error occurred
     */
    public static boolean userExists(String u) {
        String usernameCheck = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(usernameCheck)) {
            ps.setString(1, u);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking user existence.", e);
            return false;
        }
    }

    /**
     * Verifies a username and password against the stored information.
     * <p>
     * Uses a parameterized query so user input is never concatenated into the
     * SQL string (CERT rule IDS00-J: prevent SQL injection).
     * </p>
     *
     * @param unme the username entered by the user
     * @param pass the password entered by the user
     * @return {@code true} if a user with the given username and password
     *         exists; {@code false} if the credentials do not match, or if a
     *         database error occurred
     */
    public static boolean login(String unme, String pass) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // IDS00-J: Prevent SQL injection
            // This uses prepared statements with parameterized queries to avoid SQL
            // injection vulnerabilities.
            // Instead of concatenating user input directly into the SQL query, we use
            // placeholders (?) and set the values using ps.setString().
            ps.setString(1, unme);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Authentication error during user login.", e);
            return false;
        }
    }

    /**
     * gets a user's name.
     *
     * @param unme the username to look up
     * @return the user's name, or {@code "n/a"} if no such user exists, the
     *         name is not available, or a database error occurred
     */
    public static String getName(String unme) {
        String sql = ("SELECT name FROM users WHERE username = ?");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("name");
                return "n/a";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user profile data.", e);
            return "n/a";
        }

    }

    /**
     * gets a user's address.
     *
     * @param unme the username to look up
     * @return the user's address, or {@code "n/a"} if no such user exists or
     *         a database error occurred
     */
    public static String getAddress(String unme) {
        String sql = ("SELECT address FROM users WHERE username = ?");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("address");
                return "n/a";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user profile data.", e);
            return "n/a";
        }

    }

    /**
     * gets a user's database ID.
     *
     * @param unme the username to look up
     * @return the user's ID, or {@code -1} if no such user exists or a
     *         database error occurred
     */
    public static int getUserID(String unme) {
        String sql = ("SELECT id FROM users WHERE username = ?");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt("id");
                return -1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user profile data.", e);
            return -1;
        }

    }

    /**
     * Loads the store's items from {@code listOfItems.txt} into the
     * {@code items} table.
     * <p>
     * The first two lines of the file are treated as a header and skipped.
     * Each remaining line must have the form {@code name:price:stock}. Items
     * that cannot be inserted (typically because an item with the same name
     * already exists) are skipped and logged at {@link Level#WARNING}, so the
     * method is safe to call on every startup.
     * </p>
     * <p>
     * Before reading, the file is checked to be a readable regular file, and
     * its file key is compared before and after opening to detect the file
     * being swapped in the meantime (CERT rule FIO51-J). If the file is
     * missing, unreadable or changes while opening, the error is logged and
     * the method returns without loading items. An {@link IOException}
     * during reading is logged at {@link Level#SEVERE} and terminates the
     * application with exit status 1.
     * </p>
     */
    public static void addItems() {
        Path path = Paths.get("listOfItems.txt");

        try {
            // FIO51-J: identify the file by more than its name
            BasicFileAttributes before = Files.readAttributes(path, BasicFileAttributes.class);
            if (!before.isRegularFile() || !Files.isReadable(path)) {
                logger.log(Level.SEVERE, "Item list is not a readable regular file.");
                return;
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) { // FIO02-J: auto-closes

                // FIO51-J: make sure it's still the same file after opening
                BasicFileAttributes after = Files.readAttributes(path, BasicFileAttributes.class);
                if (before.fileKey() != null && !before.fileKey().equals(after.fileKey())) {
                    logger.log(Level.SEVERE, "Item list changed while opening.");
                    return;
                }

                reader.readLine();
                reader.readLine(); // throw away the first two lines

                String line;
                while ((line = reader.readLine()) != null) { // FIO08-J: check for end of stream
                    String[] data = line.split(":");

                    String sql = "INSERT INTO items (name, price, stock) VALUES (?,?,?)";

                    try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, data[0]);
                        ps.setDouble(2, Double.parseDouble(data[1].trim()));
                        ps.setInt(3, Integer.parseInt(data[2].trim()));
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Item insertion skipped (likely duplicate item).", e);
                    }
                }
            }
        } catch (IOException e) { // FIO02-J: handle file errors instead of ignoring them
            logger.log(Level.SEVERE, "Failed to read item file.", e);
            System.exit(1);
        }
    }

    /**
     * Prints every item in the {@code items} table to standard output.
     * <p>
     * Each item is printed on its own line in the format
     * {@code id: name - $price (stock in stock)}, with a blank line before
     * and after the list. Database errors are logged and nothing further is
     * printed.
     * </p>
     */
    public static void displayListOfItems() {
        String sql = "SELECT id, name, price, stock FROM items";

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");

                System.out.println(id + ": " + name + " - $" + price + " (" + stock + " in stock)");
            }
            System.out.println();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error displaying list of items.", e);
        }
    }

    /**
     * Returns the price of an item.
     *
     * @param itemName the exact name of the item
     * @return the item's price, or {@code -1.0} if no item with that name
     *         exists or a database error occurred
     */
    public static double returnItemPrice(String itemName) {
        String sql = "SELECT price FROM items WHERE name = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("price");
                return -1.0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving item price.", e);
            return -1;
        }
    }

    /**
     * Checks whether an item with the given name exists in the {@code items}
     * table.
     *
     * @param itemName the exact name of the item
     * @return {@code true} if the item exists; {@code false} if it does not,
     *         or if a database error occurred
     */
    public static boolean checkIfItemIsInList(String itemName) {
        String sql = "SELECT 1 FROM items WHERE name = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if item is in list.", e);
            return false;
        }
    }

    /**
     * Checks whether a user has at least one payment card on file.
     *
     * @param userName the username of the account to check
     * @return {@code true} if the user has one or more saved cards;
     *         {@code false} if not, or if a database error occurred
     */
    public static boolean hasCardOnFile(String userName) // checks if a user has a card on file
    {
        String sql = """
                SELECT 1 FROM cards
                JOIN users ON cards.user_id = users.id
                WHERE users.username = ?""";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if user has a card on file.", e);
            return false;
        }
    }

    /**
     * Saves a payment card to a user's account.
     * <p>
     * Only the card holder name, last four digits and expiry date from the
     * {@link Card} are stored. On success, a confirmation message is printed
     * to standard output.
     * </p>
     *
     * @param newCard      the card to store
     * @param signedInUser the user the card belongs to
     * @return {@code true} if the card was saved; {@code false} if the insert
     *         failed, for example because the user does not exist (the ID
     *         lookup returns {@code -1}, which violates the foreign key) or
     *         a database error occurred
     */
    public static boolean addUserCard(Card newCard, User signedInUser) {
        String sql = "INSERT INTO cards (user_id, card_holder, last_four, expiry) VALUES (?,?,?,?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Controller.getUserID(signedInUser.getUsername()));
            ps.setString(2, newCard.getCard_holder());
            ps.setString(3, newCard.getLast_four());
            ps.setString(4, newCard.getExpiry());
            ps.executeUpdate();

            System.out.println("Sucessfully added card to account!");
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding user card.", e);
            return false; // could be that a userID doesn't exist
        }
    }

    /**
     * Prints all payment cards saved for a user to standard output.
     * <p>
     * Each card is printed in the format
     * {@code id: holder - card ending in lastFour (exp expiry)}. If the user
     * has no cards, {@code "No cards on file"} is printed instead. Database
     * errors are logged and nothing further is printed.
     * </p>
     *
     * @param userName the username of the account whose cards should be shown
     */
    public static void displayUserCardsOnFile(String userName) {
        String sql = """
                SELECT cards.id, cards.card_holder, cards.last_four, cards.expiry
                FROM cards
                JOIN users ON cards.user_id = users.id
                WHERE users.username = ?""";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("id");
                    String holder = rs.getString("card_holder");
                    String lastFour = rs.getString("last_four");
                    String expiry = rs.getString("expiry");

                    System.out.println(id + ": " + holder + " - card ending in " + lastFour + " (exp " + expiry + ")");
                }

                if (!found) {
                    System.out.println("No cards on file");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error displaying user cards on file.", e);
        }

    }

    /**
     * Returns the number of units of an item currently in stock.
     *
     * @param itemName the exact name of the item
     * @return the stock count, or {@code -1} if no item with that name exists
     *         or a database error occurred
     */
    public static int getStockAmount(String itemName) {
        String sql = "SELECT stock FROM items WHERE name = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
                return -1; // no item with that name
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving stock amount for item.", e);
            return -1;
        }
    }

}
