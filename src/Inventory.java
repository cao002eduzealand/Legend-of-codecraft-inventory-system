import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;


public class Inventory {

    // Constants for maximum weight and slots in the inventory
    private static final int max_weight = 50;
    private static final int max_slots = 192;
    private static int start_slots = 32;

    // List to hold items in the inventory
    private List<Item> items = new ArrayList<>();

    // Getter method to return the list of items in the inventory
    public List<Item> getItems() {
        return items;
    }

    // Static variables to manage available slots and current weight
    private static int availableSlots;
    private static double currentWeight;

    // User ID to associate inventory with a particular user
    private int User_id;

    // Constructor initializes the inventory with user ID, starting slots, and weight
    public Inventory(int User_id) throws SQLException {
        this.User_id = User_id;
        this.availableSlots = start_slots;
        this.currentWeight = 0;
        loadItemsFromDBInventory();
    }

    // Loads the items from the database associated with the user
    public void loadItemsFromDBInventory() throws SQLException {
        items.clear();  // Clear the existing items in the inventory
        currentWeight = 0;  // Reset the current weight

        // Query to fetch items from the database
        String query = "SELECT Items.id AS Item_id, Items.name, Items.weight, Items.type, Items.rarity FROM Items" +
                " INNER JOIN Inventory ON Items.id = Inventory.Item_id" +
                " WHERE Inventory.User_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(query)) {

            prepareStatement.setInt(1, User_id);  // Set the user ID in the query
            try (ResultSet resultSet = prepareStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Fetch the item details from the result set
                    int itemId = resultSet.getInt("Item_id");
                    String name = resultSet.getString("name");
                    double weight = resultSet.getDouble("weight");
                    String typeString = resultSet.getString("type");
                    String rarityString = resultSet.getString("rarity");

                    // Handle the type string to match the enum
                    if ("1_HAND".equals(typeString)) {
                        typeString = "ONE_HAND";
                    } else if ("2_HAND".equals(typeString)) {
                        typeString = "TWO_HAND";
                    }

                    // Convert the string values to enum values
                    Item.Rarity rarity = Item.Rarity.valueOf(rarityString.toUpperCase());
                    Item.Type type = Item.Type.valueOf(typeString.toUpperCase());

                    // Create a new item and add it to the inventory
                    Item item = new Item(itemId, name, weight, type, rarity);
                    items.add(item);
                    currentWeight += item.getWeight();  // Update the current weight
                }
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                System.out.println("Error: "+e.getMessage());
            }
        }
    }

    // Method to add an item to the inventory
    public boolean addItem(Item item) {
        // Check if there is space in the inventory
        if (items.size() >= availableSlots) {
            System.out.println("No slots available, buy or earn more slots");
            return false;
        }
        // Check if the item is over the weight limit
        if (currentWeight + item.getWeight() > max_weight) {
            System.out.println("The item is too heavy! you can not lift more than " + max_weight + " kg.");
            return false;
        }
        // Add the item and update the weight
        items.add(item);
        currentWeight += item.getWeight();
        System.out.println(item.getName() + " added to inventory");

        // Insert the item into the database for the user
        String sql = "INSERT INTO Inventory (User_id, Item_id) VALUES (?,?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            prepareStatement.setInt(1, User_id);
            prepareStatement.setInt(2, item.getId());
            prepareStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            System.out.println("Error: "+e.getMessage());
        }
        return false;
    }

    // Removes an item from the user's inventory
    public static void removeItem(Users users, int id) {
        String deleteQuery = "DELETE FROM Inventory WHERE User_id = ? AND Item_id = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            deleteStatement.setInt(1, users.getId());  // Set the user ID
            deleteStatement.setInt(2, id);  // Set the item ID

            // Execute the delete query
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Item removed from inventory");
            } else {
                System.out.println("You entered wrong");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            System.out.println("Error: "+e.getMessage());
        }
    }

    // Unlock more slots for the user
    public static boolean unlockSlots(Users users) {
        Scanner scanner = new Scanner(System.in);

        // Display current slots
        System.out.println("Your current amount of slots is: " + availableSlots);
        System.out.println("How many slots would you like to buy? ");

        int slotsToUnlock = scanner.nextInt();
        double slotCost = 20.0;
        double totalSlotCost = slotCost * slotsToUnlock;

        // Check for valid slot purchases
        if (slotsToUnlock <= 0) {
            System.out.println("You have to buy at least 1 slot");
            return false;
        }

        // Check if the new slot count is over the maximum
        if (availableSlots + slotsToUnlock > max_slots) {
            System.out.println("Can't unlock more than " + max_slots + " slots.");
            return false;
        }
        // Check if the user has enough money to buy
        if (users.getBalance() < totalSlotCost) {
            System.out.println("You do not have enough money to unlock slots");
            return false;
        }

        // Update the available slots and user balance
        availableSlots += slotsToUnlock;
        users.setBalance(users.getBalance() - totalSlotCost);

        // Update the user balance in the database
        String updateBalanceSQL = "UPDATE Users SET BALANCE = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateBalanceSQL)) {

            statement.setDouble(1, users.getBalance());
            statement.setInt(2, users.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            System.out.println("Error: "+e.getMessage());
            return false;
        }

        System.out.println(slotsToUnlock + " slots have been unlocked, you have " + availableSlots + " slots.");
        System.out.println("New balance: " + users.getBalance());
        return true;
    }

    // Displays the details of the inventory
    public void displayInventory() {
        System.out.println("Querying inventory for User ID: " + User_id);
        try {
            loadItemsFromDBInventory();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Display inventory details
        System.out.println("----- Inventory Status -----");
        System.out.println("Amount of slots in use: " + items.size() + "/" + availableSlots);
        System.out.println("Total weight: " + currentWeight + "/" + max_weight + " kg");
        System.out.println("Items in your inventory:");
        for (Item item : items) {
            System.out.println("- " + item.getId() + ", " + item.getName() + " (" + item.getWeight() + " kg)");
        }
    }

    // Method to calculate the survival bonus based on item rarity. (NOT WORKING, IGNORE METHOD.  MISSING EQUIPLOADOUTMETHOD).
    public int calculateSurvivalBonus() {
        int bonus = 0;

        // Iterate through items to calculate survival bonus based on rarity
        for (Item item : items) {
            switch (item.getRarity()) {
                case COMMON:
                    bonus += 1; // Common items add 1% bonus
                    break;
                case UNCOMMON:
                    bonus += 2; // Uncommon items add 2% bonus
                    break;
                case RARE:
                    bonus += 4; // Rare items add 4% bonus
                    break;
                case EPIC:
                    bonus += 6; // Epic items add 6% bonus
                    break;
                case LEGENDARY:
                    bonus += 8; // Legendary items add 8% bonus
                    break;
            }
        }
        return bonus;
    }

    // Method to sort items in the inventory based on different attributes
    public static void sortItems(Users users, Scanner scanner) {
        List<Item> allOwnedItems = new ArrayList<>();
        String query = "SELECT Items.id AS Item_id, Items.name, Items.weight, Items.type, Items.rarity FROM Items" +
                " INNER JOIN Inventory ON Items.id = Inventory.Item_id" +
                " WHERE Inventory.User_id = ?";

        // Fetch all items from the database for the user
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, users.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int itemId = resultSet.getInt("Item_id");
                    String name = resultSet.getString("name");
                    double weight = resultSet.getDouble("weight");
                    String typeString = resultSet.getString("type");
                    String rarityString = resultSet.getString("rarity");

                    if ("1_HAND".equals(typeString)) {
                        typeString = "ONE_HAND";
                    } else if ("2_HAND".equals(typeString)) {
                        typeString = "TWO_HAND";
                    }

                    Item.Rarity rarity = Item.Rarity.valueOf(rarityString.toUpperCase());
                    Item.Type type = Item.Type.valueOf(typeString.toUpperCase());

                    // Create a new item and add it to the list
                    Item item = new Item(itemId, name, weight, type, rarity);
                    allOwnedItems.add(item);
                }

                // Ask the user if they want to sort items
                System.out.println("Do you want to sort your items for a better overview? (yes/no)");
                String choice = scanner.nextLine().toLowerCase();
                System.out.println("User choice: " + choice);

                // If answer yes, offer sorting options
                if (choice.equalsIgnoreCase("yes")) {
                    System.out.println("Type 1 to sort by name.\nType 2 to sort by rarity.\nType 3 to sort by weight.\nType 4 to sort by type.");
                    int choice1 = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Sorting option chosen: " + choice1);

                    switch (choice1) {
                        case 1:
                            sortItemsByName(allOwnedItems, scanner);
                            break;

                        case 2:
                            sortItemsByRarity(allOwnedItems, scanner);
                            break;

                        case 3:
                            sortItemsByWeight(allOwnedItems, scanner);
                            break;

                        case 4:
                            sortItemsByType(allOwnedItems, scanner);
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    // Sorting items by name
    public static void sortItemsByName(List<Item> list, Scanner scanner) {

        System.out.println("Type 1 for Alphabetical order.\nType 2 for reverse order.");
        int choice = scanner.nextInt();
        scanner.nextLine();
//Alphabetical order
        if (choice == 1) {
            list.sort(Comparator.comparing(Item::getName));
            System.out.println("Items have been sorted by name.");
            //Reverse alphabetic order
        } else if (choice == 2) {
            list.sort(Comparator.comparing(Item::getName).reversed());
            System.out.println("Items have been sorted in reverse order.");
        }

        // Print sorted list
        for (Item item : list) {
            System.out.println(item.getName());
        }
    }

    // Sorting items by rarity (from low to high or vice versa)
    public static void sortItemsByRarity(List<Item> list, Scanner scanner) {
        System.out.println("Type 1 to sort from bad to good rarity. \nType 2 to sort from good to bad rarity.");
        int choice = scanner.nextInt();
        scanner.nextLine();

//sort item from low rarity to good
        if (choice == 1) {
            list.sort(Comparator.comparing(Item::getRarity));
            System.out.println("Items have been sorted from bad to good rarity.");

            //sort items from good to low rarirty
        } else if (choice == 2) {
            list.sort(Comparator.comparing(Item::getRarity).reversed());
            System.out.println("Items have been sorted from good to bad.");
        }

        // Print sorted list
        for (Item item : list) {
            System.out.println(item.getRarity().toString());
        }
    }

    // Sorting items by weight
    public static void sortItemsByWeight(List<Item> list, Scanner scanner) {
        System.out.println("Type 1 to sort from low to high weight. \nType 2 to sort from high to low weight.");
        int choice = scanner.nextInt();
        scanner.nextLine();

        //sort items by lowest weight to highest
        if (choice == 1) {
            list.sort(Comparator.comparing(Item::getWeight));
            System.out.println("Items have been sorted from low to high weight.");

            //sort items from heaviest to lightest
        } else if (choice == 2) {
            list.sort(Comparator.comparing(Item::getWeight).reversed());
            System.out.println("Items have been sorted from high to low weight.");
        }

        // Print sorted list
        for (Item item : list) {
            System.out.println(item.getWeight());
        }
    }

    // Sorting items by type
    public static void sortItemsByType(List<Item> list, Scanner scanner) {
        System.out.println("Type 1 to sort from non-handle to more-handle type. \nType 2 to sort from more-handle to non-handle type.");
        int choice = scanner.nextInt();
        scanner.nextLine();

        //sort items from OFF_HAND to 1_HAND to 2_HAND
        if (choice == 1) {
            list.sort(Comparator.comparing(Item::getType));
            System.out.println("Items have been sorted from non-handle to more-handle.");

            // sort items from 2_HAND to 1_HAND to OFF_HAND
        } else if (choice == 2) {
            list.sort(Comparator.comparing(Item::getType).reversed());
            System.out.println("Items have been sorted from more-handle to non-handle.");
        }

        // Print sorted list
        for (Item item : list) {
            System.out.println(item.getType());
        }
    }
}
