import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {

    // Starts the main game menu and handles user interactions
    public static void startGame(Users users) {
        Scanner input = new Scanner(System.in);
        boolean inGame = true; // Tracks if the user is still in the game menu

        while (inGame) {
            System.out.println("\n----< Welcome to the game! >----");
            System.out.println("1. View Inventory");
            System.out.println("2. Enter the Arena");
            System.out.println("3. Enter the Shop");
            System.out.println("4. Logout");
            System.out.println("Choose an option: ");

            try {
                // Get the user's menu choice
                int choice = input.nextInt();
                input.nextLine(); // Consume the newline character

                // Handle the user's choice
                switch (choice) {

                    case 1:
                        // Display the user's inventory and balance
                        users.getInventory().displayInventory();
                        System.out.println("Your current balance: " + users.getBalance() + " gold");

                        // Allow the user to sort items
                        Inventory.sortItems(users, input);

                        // Ask if the user wants to delete an item
                        System.out.println("Do you want to delete an item? (yes/no)");
                        String confirm = input.nextLine().toLowerCase();

                        if (confirm.equals("yes")) {
                            System.out.println("Insert the ID of the item you want to remove. (Only 1 gets deleted). Press 0 to cancel.");
                            int id = input.nextInt();

                            if (id == 0) {
                                System.out.println("Cancelling....");
                                break;
                            }

                            // Remove the selected item from the inventory
                            Inventory.removeItem(users, id);
                        }
                        break;

                    case 2:
                        // Start the arena
                        Arena.start(users);
                        break;

                    case 3:
                        // Open the shop
                        Shop.openShop(users);
                        break;

                    case 4:
                        // Logout from the game. press 4, the menu comes up again, press 4 again then it logs out.
                        System.out.println("Logging out....");
                        inGame = false;
                        break;

                    default:
                        // Handle invalid menu options
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (InputMismatchException e) {
                // Handle invalid input (e.g., entering text instead of a number)
                System.out.println("Invalid input, please type a number.");
                input.nextLine(); // Clear the input buffer
            }
        }
    }
}