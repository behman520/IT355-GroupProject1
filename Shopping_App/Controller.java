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

public final class Controller {

    private Controller() { }

    private static final String URL = "jdbc:sqlite:Database/Database.db";
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    private static Connection connect() throws SQLException {

        new java.io.File("Database").mkdirs(); // make the database foler if it doesn't exist

        Connection conn = DriverManager.getConnection(URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    // runs during startup to make sure the tables exist (if not creating them)
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

    // add a user to the database
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

    // checks if a username already exists
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

    public static boolean login(String unme, String pass) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            //IDS00-J: Prevent SQL injection
            // This uses prepared statements with parameterized queries to avoid SQL injection vulnerabilities.
            // Instead of concatenating user input directly into the SQL query, we use placeholders (?) and set the values using ps.setString().
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

    // gets a user's name
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

    // gets a user's address
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

    // gets a user's ID
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

    public static int getStockAmount(String itemName)
    {
        String sql = "SELECT stock FROM items WHERE name = ?";

        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, itemName);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    return rs.getInt("stock");
                }
                return -1; //no item with that name
            }
        }
        catch(SQLException e)
        {
            logger.log(Level.SEVERE, "Error retrieving stock amount for item.", e);
            return -1;
        }
    }

}
