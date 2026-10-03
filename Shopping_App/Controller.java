
//this interacts with the Database.db file


import java.sql.Connection;
import java.sql.DriverManager;
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
                username TEXT NOT NULL UNIQUE,
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

    

}
