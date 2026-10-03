
//this interacts with the Database.db file


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    private static final String URL = "jdbc:sqlite:Database/Database.db";

    private static Connection connect() throws SQLException {
        new java.io.File("Database").mkdirs();   // creates the folder if missing
        return DriverManager.getConnection(URL);
    }

    //runs once during startup to make sure the tables exist
    public static void createTables()
    {
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
                name TEXT NOT NULL, 
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

        try(Connection conn = connect(); Statement stmt = conn.createStatement())
        {
            stmt.execute(users);
            stmt.execute(items);
            stmt.execute(orders);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    //add a user to the database
    public static boolean addUser(User u, String password)
    {
        String name = u.getName();
        String username = u.getUsername();
        System.out.println("username: " +username);
        String address = u.getAddress();

        String sql ="INSERT INTO users (name, username, address, password) VALUES (?,?,?,?)";
        
        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) //adding the user to the database
        {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, address);
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        }
        catch(SQLException e) //couldn't add user for some reason
        {
            System.out.println("Could not add user: " + e.getMessage());
            return false;
        }
    }

    //checks if a username already exists
    public static boolean userExists(String u)
    {
        String usernameCheck = "SELECT 1 FROM users WHERE username = ?";

        try(Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(usernameCheck)){
        ps.setString(1, u);
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
}
