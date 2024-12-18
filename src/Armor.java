import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// inherites from Item.
public class Armor extends Item{

    //constructor that inherites from Item class. Not in use
    public Armor(int id, String name, double weight, Type type, Rarity rarity){
        super(id, name, weight, type, rarity);
    }

    //method to create an Item. // Only for display purpose.
    public static void createItemArmor() {
        //sql query to insert item into Items table
        String sql = "INSERT INTO Items(name, weight, type, rarity) VALUES(?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            //setting the values.
            preparedStatement.setString(1, "Wood-Chestplate");
            preparedStatement.setDouble(2, 5);
            preparedStatement.setString(3, "OFF_HAND");
            preparedStatement.setString(4, "COMMON");

            //the statement gets executed
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            //print error message
            e.printStackTrace();
            System.out.println("Error: "+e.getMessage());
        }
    }
}



