
//this interacts with the Database.db file

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Controller {
    private static final String URL = "jdbc:sqlite:Database/Database.db";

    private static Connection connect() throws SQLException {
        
        new java.io.File("Database").mkdirs(); //make the database foler if it doesn't exist
        
        Connection conn = DriverManager.getConnection(URL);

        try(Statement stmt = conn.createStatement())
        {
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
                """; //cards are deleted if a users account is deleted

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(items);
            stmt.execute(orders);
            stmt.execute(cards);
        } catch (SQLException e) {
            e.printStackTrace();
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
            System.out.println("Could not add user: " + e.getMessage());
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
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(String unme, String pass) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // gets a user's name
    public static String getName(String unme) {
        String sql = ("SELECT name FROM users WHERE username = ?");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return rs.getString("name");
                return "n/a";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "n/a";
        }

    }

    // gets a user's address
    public static String getAddress(String unme) {
        String sql = ("SELECT address FROM users WHERE username = ?");

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, unme);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return rs.getString("address");
                return "n/a";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "n/a";
        }

    }

    public static void addItems() // adds the list of items to the db
    {
        File textFile = new File("listOfItems.txt"); // get the list of items as a File

        try(Scanner scan = new Scanner(textFile)) {
 
            if (textFile.exists()) // checking the return value of the file instead of just assuming it exists
            {                      // FIO02-J ^^^^^***********************************^^^^^

                System.out.println("File exists");
 
                scan.nextLine();
                scan.nextLine(); // throw away the first two lines
                while (scan.hasNextLine()) {
                    String[] data = scan.nextLine().split(":");

                    String sql = "INSERT INTO items (name, price, stock) VALUES (?,?,?)";

                    try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, data[0]);
                        ps.setDouble(2, Double.parseDouble(data[1].trim()));
                        ps.setInt(3, Integer.parseInt(data[2].trim()));
                        ps.executeUpdate();

                    } catch (SQLException e) {
                      //should only go here when we try to insert the items int the db that already exist
                    }
                }

                scan.close();
            }
            else
            {
                System.out.println("Couldn't open list of items, exiting");
                System.exit(1);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

         
    }

    public static void displayListOfItems()
    {
        String sql = "SELECT id, name, price, stock FROM items";

 
        try(Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql))
        {
            System.out.println();
            while(rs.next())
            {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");

                System.out.println(id + ": " + name + " - $" + price + " (" + stock + " in stock)");
            }
            System.out.println();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }


    public static double returnItemPrice(String itemName)
    {
        String sql = "SELECT price FROM items WHERE name = ?";

         try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemName);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return rs.getDouble("price");
                return -1.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
 
    public static boolean checkIfItemIsInList(String itemName)
    {
        String sql = "SELECT 1 FROM items WHERE name = ?";

        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1,itemName);

            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasCardOnFile(String userName) //checks if a user has a card on file
    {
        String sql = """
                SELECT 1 FROM cards 
                JOIN users ON cards.user_id = users.id
                WHERE users.username = ?""";

        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, userName);

            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addCard(Card newCard, User signedInUser)
    {
        String sql = "INSERT INTO cards (user_id, card_holder, last_four, expiry) VALUES (?,?,?,?)";

        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, );
            ps.setString(2, newCard.getCard_holder());

        }
    }
}
