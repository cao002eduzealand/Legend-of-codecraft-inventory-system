import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Arena {

    // Starts the arena and handles the gameplay flow
    public static void start(Users user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("----- Welcome to the Arena -----");
        System.out.println("Choose difficulty: \n1. Easy\n2. Medium\n3. Hard\n4. Nightmare");

        int chooseDifficulty = -1;
        try {
            // Get difficulty choice from the user
            chooseDifficulty = scanner.nextInt();
            scanner.nextLine(); // Consume leftover newline
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid difficulty");
            return; // Exit if invalid input is provided
        }

        // Generate the survival chance based on difficulty and user's inventory
        int survivalChance = generateSurvivalChance(chooseDifficulty, user);
        Random random = new Random();

        // Determine if the user won or lost based on survival chance
        if (random.nextInt(100) + 1 <= survivalChance) {
            System.out.println("You have won!");

            // Determine the rarity of the reward based on chosen difficulty
            String rarity;
            switch (chooseDifficulty) {
                case 1: rarity = "COMMON"; break;
                case 2: rarity = "UNCOMMON"; break;
                case 3: rarity = "EPIC"; break;
                case 4: rarity = "LEGENDARY"; break;
                default: rarity = "COMMON"; break;
            }

            // Reward the user with gold and an item of the determined rarity
            rewardUser(user, rarity);
        } else {
            // Inform the user if they lose the battle
            System.out.println("You have lost.");
        }
    }

    // Calculates survival chance based on difficulty and inventory bonuses
    private static int generateSurvivalChance(int chooseDifficulty, Users user) {
        int baseChance;

        // Assign base survival chance based on difficulty
        switch (chooseDifficulty) {
            case 1: baseChance = 80; break; // Easy
            case 2: baseChance = 60; break; // Medium
            case 3: baseChance = 40; break; // Hard
            case 4: baseChance = 20; break; // Nightmare
            default: baseChance = 80; break; // Default to Easy
        }

        // Calculate bonus survival chance from the user's inventory
        int bonus = 0;
        if (user.getInventory() != null) {
            bonus = user.getInventory().calculateSurvivalBonus();
        }

        // Ensure the total chance does not exceed 100%
        int totalChance = Math.min(100, baseChance + bonus);

        // Display calculated chances for debugging and feedback
        System.out.println("Base Chance: " + baseChance + "%, Bonus: " + bonus + "%, Total Chance: " + totalChance + "%");

        return totalChance;
    }

    // Rewards the user with a random item and gold
    private static void rewardUser(Users user, String rarity) {
        Random random = new Random();

        // Generate a random reward amount of gold between 50 and 150
        double balanceReward = random.nextInt(100) + 50;
        System.out.println("You have received " + balanceReward + " gold!");

        // Fetch a random item of the given rarity from the database
        Item rewardedItem = fetchRandomItemFromDatabase(rarity);
        if (rewardedItem == null) {
            System.out.println("Failed to fetch a valid item for rarity: " + rarity);
            return; // Exit if no item is found
        }

        // Attempt to add the rewarded item to the user's inventory
        if (user.getInventory().addItem(rewardedItem)) {
            System.out.println("You have received a new item: " + rewardedItem.getName());

            // Reload inventory from the database to ensure consistency
            try {
                user.getInventory().loadItemsFromDBInventory();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Inform the user if their inventory is full
            System.out.println("Your inventory is full. The item could not be added.");
        }

        // Update the user's balance in the database with the reward
        updateUserBalanceInDatabase(user, balanceReward);
    }

    // Fetches a random item from the database based on the specified rarity
    public static Item fetchRandomItemFromDatabase(String rarity) {
        String sql = "SELECT id, name, weight, type, rarity FROM Items WHERE rarity = ? ORDER BY RAND() LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the rarity parameter for the query
            statement.setString(1, rarity.toUpperCase());

            ResultSet resultSet = statement.executeQuery();

            // Process the result if an item is found
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double weight = resultSet.getDouble("weight");
                String typeString = resultSet.getString("type");

                // Convert the type string from the database to the Item.Type enum
                Item.Type type;
                switch (typeString) {
                    case "1_HAND": type = Item.Type.ONE_HAND; break;
                    case "2_HAND": type = Item.Type.TWO_HAND; break;
                    case "OFF_HAND": type = Item.Type.OFF_HAND; break;
                    default: throw new IllegalArgumentException("Invalid type in database: " + typeString);
                }

                // Convert rarity to the Item.Rarity enum
                Item.Rarity itemRarity = Item.Rarity.valueOf(rarity.toUpperCase());

                // Create and return the item
                return new Item(id, name, weight, type, itemRarity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return null; // Return null if no item is found
    }

    // Updates the user's balance in the database after receiving a reward
    private static void updateUserBalanceInDatabase(Users user, double reward) {
        String getBalancesql = "SELECT balance FROM Users WHERE id = ?";
        String updateBalancesql = "UPDATE Users SET balance = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement getStatement = connection.prepareStatement(getBalancesql);
             PreparedStatement updateStatement = connection.prepareStatement(updateBalancesql)) {

            // Retrieve the current balance from the database
            getStatement.setInt(1, user.getId());
            try (ResultSet resultSet = getStatement.executeQuery()) {
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");

                    // Calculate the new balance by adding the reward
                    double newBalance = currentBalance + reward;

                    // Update the new balance in the database
                    updateStatement.setDouble(1, newBalance);
                    updateStatement.setInt(2, user.getId());
                    updateStatement.executeUpdate();

                    // Update the balance in the user's object
                    user.setBalance(newBalance);

                    System.out.println("Balance updated successfully: " + newBalance + " gold");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}