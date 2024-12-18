import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database URL pointing to the Inventory database
    private static final String URL = "jdbc:mysql://localhost:3306/Inventory";

    // Username and password for connecting to the database
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Provides a connection to the database
    public static Connection getConnection() {
        try {
            // Attempt to establish a connection using the provided credentials
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // Print the stack trace if a connection error occurs and return null
            e.printStackTrace();
            return null;
        }
    }
}