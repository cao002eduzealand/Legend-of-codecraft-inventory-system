import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    // Method to handle user registration
    public void Register(Users users) {

        // SQL Queries:
        // 1. Check if the username already exists
        String checkSql = "SELECT * FROM Users WHERE name=?";
        // 2. Insert new user into the Users table
        String insertSql = "INSERT INTO Users (name, password) VALUES (?,?)";
        // 3. Insert default items into the user's inventory after successful registration
        String insertToInventorysql = "INSERT INTO Inventory (User_id, Item_id) VALUES (?,?)";

        // Establishing connection and prepared statements
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkSql); // Check if username exists
             PreparedStatement insertStatement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS); // Insert user
             PreparedStatement insertToInventoryStatement = connection.prepareStatement(insertToInventorysql)) { // Add starter items

            // Set the username in the check query
            checkStatement.setString(1, users.getName());

            // Execute query to check if the username exists
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If the username exists, print a failure message and exit the method
                    System.out.println("Registration Failed: Username already taken.");
                    return;
                }
            }

            // Set the parameters for inserting a new user (username and password)
            insertStatement.setString(1, users.getName());
            insertStatement.setString(2, users.getPassword());

            // Execute the insert query
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                // If insertion is successful, print a success message
                System.out.println("Registration Successful");

                // Retrieve the generated User ID for the newly inserted user
                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int User_id = generatedKeys.getInt(1); // Get the generated user ID
                        users.getId(User_id); // Update the user's ID

                        // Create a new inventory instance for the user
                        Inventory inventory = new Inventory(User_id);
                        users.setInventory(inventory); // Link inventory to the user

                        // Define default item IDs (starter items)
                        int SwordId = 1;
                        int ArmorId = 6;
                        int PotionId = 5;

                        // Add default items to the user's inventory
                        insertToInventoryStatement.setInt(1, User_id);
                        insertToInventoryStatement.setInt(2, SwordId);
                        insertToInventoryStatement.executeUpdate();

                        insertToInventoryStatement.setInt(2, ArmorId);
                        insertToInventoryStatement.executeUpdate();

                        insertToInventoryStatement.setInt(2, PotionId);
                        insertToInventoryStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions that may occur
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method to handle user login
    public void logIn(Users users) throws SQLException {

        // SQL Query: Check if the username and password match an existing user
        String sql = "SELECT * FROM Users WHERE name = ? AND password = ?";

        // Establish connection and prepare statement
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the parameters for the query: username and password
            preparedStatement.setString(1, users.getName());
            preparedStatement.setString(2, users.getPassword());

            // Execute query to check if user information is valid
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // If user exists, print login success message
                    System.out.println("Login Successful");

                    // Retrieve user data from the database
                    int User_id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    double balance = resultSet.getDouble("balance");

                    // Update the Users object with the retrieved data
                    users.setId(User_id);
                    users.setName(name);
                    users.setPassword(password);
                    users.setBalance(balance);
                } else {
                    // If user information is invalid, print failure message
                    System.out.println("Login Failed");
                }
            } catch (SQLException e) {
                // Handle SQL exceptions for the query execution
                e.printStackTrace();
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}