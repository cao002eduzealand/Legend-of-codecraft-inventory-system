import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Shop {

    // Scanner object for taking user input.
    private static Scanner input = new Scanner(System.in);

    // Prices for different types of chests.
    private static final double COMMON_CHEST_PRICE = 50;
    private static final double UNCOMMON_CHEST_PRICE = 200;
    private static final double RARE_CHEST_PRICE = 450;
    private static final double EPIC_CHEST_PRICE = 850;
    private static final double LEGENDARY_CHEST_PRICE = 1250;

    // Opens the shop for the user, allowing them to perform various actions such as viewing available items,
    // buying chests, or unlocking more inventory slots.
    public static void openShop(Users user) {
        boolean inShop = true;

        // Loop that keeps the shop menu open until the user decides to exit.
        while (inShop) {
            System.out.println();
            System.out.println("----< Welcome to the shop! >----");
            System.out.println("Here are your options:\nPress 1 to see available items.\nPress 2 to buy a chest.\nPress 3 to buy more slots for inventory.\nPress 4 to return to main menu.");

            int option = -1;

            try {
                // Get the user's option from the input
                option = input.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid option!");  // Handle invalid input types
                e.printStackTrace();
            }
            input.nextLine(); // Clear the buffer to avoid issues with inputs

            // Handle the user's choice based on the option they selected
            switch (option) {
                case 1:
                    readItems();  // Display all the available items in the shop
                    break;
                case 2:
                    openChest(user);  // Open the chest shop where users can purchase different types of chests
                    break;
                case 3:
                    Inventory.unlockSlots(user);  // Unlock more inventory slots for the user (if they have enough resources)
                    break;
                case 4:
                    inShop = false;  // Exit the shop menu
                    break;
                default:
                    System.out.println("Invalid option!");  // If the user enters an invalid option, prompt again.
            }
        }
    }

    // Reads all available items from the database and prints them to the console.
    // The method fetches item details such as name, weight, type, and rarity from the database.

    private static List<Item> readItems() {
        List<Item> allItems = new ArrayList<>();  // List to store all items

        String sql = "SELECT * FROM Items";  // SQL query to get all items from the Items table.

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Process each row from the result set
            while (resultSet.next()) {
                int id = resultSet.getInt("id");  // Get the item ID
                String name = resultSet.getString("name");  // Get the item name
                double weight = resultSet.getDouble("weight");  // Get the item weight
                String typeString = resultSet.getString("type");  // Get the item type as a string
                String rarityString = resultSet.getString("rarity");  // Get the rarity of the item

                // Convert the type and rarity strings to enum values
                Item.Rarity rarity = Item.Rarity.valueOf(rarityString);

                // Switch block to convert string type into the Item.Type enum
                Item.Type type;
                switch (typeString) {
                    case "1_HAND":
                        type = Item.Type.ONE_HAND;
                        break;
                    case "2_HAND":
                        type = Item.Type.TWO_HAND;
                        break;
                    case "OFF_HAND":
                        type = Item.Type.OFF_HAND;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type: " + typeString);  // Error if an unknown type is found.
                }

                // Create a new Item object and add it to the list
                allItems.add(new Item(id, name, weight, type, rarity));
            }

            // Print out all the items in the shop
            for (Item item : allItems) {
                System.out.println(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());  // Handle SQL errors (Example, database issues)
        } catch (IllegalArgumentException e) {
            System.out.println("There was an error: Invalid type or rarity found.");  // Handle any errors in type/rarity conversion
        }

        return allItems;  // Return the list of all available items
    }

    // Opens the chest shop where the user can buy different types of chests (with different rarity levels).
    // The user can buy a chest by selecting an option, and they will receive an item based on the chest they buy.

    private static void openChest(Users user) {
        while (true) {
            System.out.println("----< Welcome to the chest shop >----");
            System.out.println("1. Buy a Common chest (" + COMMON_CHEST_PRICE + " gold)");
            System.out.println("2. Buy an Uncommon chest (" + UNCOMMON_CHEST_PRICE + " gold)");
            System.out.println("3. Buy a Rare chest (" + RARE_CHEST_PRICE + " gold)");
            System.out.println("4. Buy an Epic chest (" + EPIC_CHEST_PRICE + " gold)");
            System.out.println("5. Buy a Legendary chest (" + LEGENDARY_CHEST_PRICE + " gold)");
            System.out.println("6. Return");
            System.out.println("Choose an option:");

            int choice = -1;
            try {
                choice = input.nextInt();  // Get the user's choice
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice, please try again.");  // Handle invalid input
                e.printStackTrace();
            }
            input.nextLine();  // Clear the input buffer for the next input

            // Handle each chest-buying option based on user's choice
            switch (choice) {
                case 1:
                    buyChest(user, "COMMON", COMMON_CHEST_PRICE);  // Buy a Common chest
                    break;
                case 2:
                    buyChest(user, "UNCOMMON", UNCOMMON_CHEST_PRICE);  // Buy an Uncommon chest
                    break;
                case 3:
                    buyChest(user, "RARE", RARE_CHEST_PRICE);  // Buy a Rare chest
                    break;
                case 4:
                    buyChest(user, "EPIC", EPIC_CHEST_PRICE);  // Buy an Epic chest
                    break;
                case 5:
                    buyChest(user, "LEGENDARY", LEGENDARY_CHEST_PRICE);  // Buy a Legendary chest
                    break;
                case 6:
                    System.out.println("Thank you for visiting the shop!");  // Exit the chest shop
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");  // Handle invalid choices
            }
        }
    }

    // Handles the logic for buying a chest. It checks if the user has enough balance,
    // fetches a random item based on the chest rarity, and updates the user's inventory and balance.
    private static void buyChest(Users user, String rarity, double price) {
        // Check if the user has enough gold to buy the chest
        if (user.getBalance() < price) {
            System.out.println("You don't have enough gold to buy this chest.");
            return;
        }

        // Fetch a random item based on the chest rarity from the database
        Item rewardedItem = Arena.fetchRandomItemFromDatabase(rarity);
        if (rewardedItem == null) {
            System.out.println("No items found for this rarity, please try again later.");
            return;
        }

        // Try to add the item to the user's inventory. If the inventory is full, show an error message.
        if (user.getInventory().addItem(rewardedItem)) {
            System.out.println("You bought a " + rarity + " chest and received: " + rewardedItem.getName());
            // Update the user's balance in the database
            updateUserBalanceInDatabase(price, user);
        } else {
            System.out.println("Your inventory is full! The item could not be added.");
        }
    }


     // Updates the user's balance in the database after a purchase.
     //cost The amount to subtract from the user's balance.
     // The user whose balance is being updated.

    private static void updateUserBalanceInDatabase(double cost, Users user) {
        String sql = "UPDATE Users SET balance = balance - ? WHERE id = ?";  // SQL query to deduct balance

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the cost and user ID for the SQL query
            statement.setDouble(1, cost);
            statement.setInt(2, user.getId());
            statement.executeUpdate();  // Execute the update

            // Update the user's balance in memory
            user.setBalance(user.getBalance() - cost);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());  // Handle database errors
        }
    }
}




