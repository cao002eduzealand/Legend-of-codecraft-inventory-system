import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Variable to store the currently logged-in user
        Users loggedInUser = null;

        // Create an instance of the Login class to handle user registration and login
        Login login = new Login();

        // Main program loop: keeps running until the user chooses to exit
        while (true) {
            // If no user is logged in, show the main menu
            if (loggedInUser == null) {
                System.out.println("----< Main Menu >----");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = -1; // Initialize choice to an invalid option

                // Handle invalid input (example entering a non-integer)
                try {
                    choice = scanner.nextInt(); // Read user choice
                    scanner.nextLine(); // Consume leftover newline character
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid option!"); // Inform user of invalid input
                    scanner.nextLine(); // Clear the scanner buffer
                    continue; // Restart the loop
                }

                // Use a separate scanner for user input
                Scanner input = new Scanner(System.in);

                // Handle user selection
                switch (choice) {

                    case 1: // User Registration
                        System.out.println("Indtast nyt brugernavn: "); // Prompt for username
                        String nyNavn = input.nextLine(); // Read the username
                        System.out.println("Indtast nyt password: "); // Prompt for password
                        String nyPassword = input.nextLine(); // Read the password

                        // Create a new Users object with default values
                        Users users = new Users(0, nyNavn, nyPassword, 0, null);

                        // Call the Register method to add the new user to the database
                        login.Register(users);

                        break;

                    case 2: // User Login
                        System.out.println("Indtast brugernavn"); // Prompt for username
                        String navn = input.nextLine(); // Read the username
                        System.out.println("Indtast password: "); // Prompt for password
                        String kode = input.nextLine(); // Read the password

                        // Create a Users object with provided login information
                        Users user = new Users(0, navn, kode, 0, null);

                        try {
                            // Call the logIn method to authenticate the user
                            login.logIn(user);

                            // Check if login was successful (user ID updated to a valid value)
                            if (user.getId() != 0) {
                                loggedInUser = user; // Set the logged-in user

                                // Initialize the user's inventory
                                Inventory inventory = new Inventory(user.getId());
                                user.setInventory(inventory); // Associate inventory with user

                                // Load the user's inventory from the database
                                inventory.loadItemsFromDBInventory();

                                // Confirmation message
                                System.out.println("You are now logged in, " + user.getName());

                                // Start the game menu for the logged-in user
                                Game.startGame(loggedInUser);
                            }
                        } catch (SQLException e) {
                            // Print the stack trace for debugging and show login failure message
                            e.printStackTrace();
                            System.out.println("Login failed "+e.getMessage());
                        }

                        break;

                    case 3: // Exit Program
                        System.out.println("Exiting...... ");
                        return; // Exit the program

                    default: // Handle invalid menu choices
                        System.out.println("Invalid option. Please try again.");
                }

            } else {
                // If a user is already logged in, start the game menu
                Game.startGame(loggedInUser);
                loggedInUser = null; // After the game ends or current user logs out, reset the logged-in user to null to continue the loop
            }
        }
    }
}