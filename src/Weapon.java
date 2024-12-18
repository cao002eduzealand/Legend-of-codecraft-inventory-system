import java.sql.Connection;
import java.sql.*;
// inherites from Item.
public class Weapon extends Item{
//constructor that inherites from Item class. Not in use
public Weapon(int id, String name, double weight, Type type, Rarity rarity){
    super(id, name, weight, type, rarity);
}
    //method to create an Item. // Only for display purpose
    public static void createItemWeapon(){
    String sql = "INSERT INTO Items(name, weight, type, rarity) VALUES(?,?,?,?)";

        // Set values for the Dragon sword
    try (Connection connection = DatabaseConnection.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, "Dragon sword");
        preparedStatement.setDouble(2, 7.5);
        preparedStatement.setString(3, "1_HAND");
        preparedStatement.setString(4, "COMMON");

        // Execute the insert operation
        preparedStatement.executeUpdate();

    } catch (SQLException e){
        //prints error message
        e.printStackTrace();
        System.out.println("error"+e.getMessage());
    }
    }


}
