import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// inherites from Item.
public class Potion extends Item {

//constructor that inherites from Item class. Not in use
public Potion(int id, String name, double weight, Type type, Rarity rarity) {
        super(id, name, weight, type, rarity);
    }

    //method to create an Item. // Only for display purpose
    public static void createItemPotion() {
        String sql = "INSERT INTO Items(name, weight, type, rarity) VALUES(?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set values for the Fire-Potion
            preparedStatement.setString(1, "Fire-Potion");   // Name
            preparedStatement.setDouble(2, 2);              // Weight
            preparedStatement.setString(3, "1_HAND");       // Type
            preparedStatement.setString(4, "COMMON");       // Rarity

            // Execute the insert operation
            preparedStatement.executeUpdate();

            System.out.println("Fire-Potion has been added to the database.");
        } catch (SQLException e) {
            // Handle any SQL exceptions that may occur
            e.printStackTrace();
            System.out.println("Error: "+e.getMessage());
        }
    }
}