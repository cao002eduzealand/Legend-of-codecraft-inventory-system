//Represents a user in the system with ID, name, password, balance, and inventory.
public class Users {

    private int id;                // Unique user ID
    private String name;           // Username
    private String password;       // User's password
    private double balance;        // User's balance (gold)
    private Inventory inventory;   // User's inventory

    // Constructor to initialize the user
    public Users(int id, String name, String password, double balance, Inventory inventory) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.inventory = inventory;
    }

    // Get the user's ID
    public int getId() {
        return id;
    }

    // Set the user's ID
    public void setId(int id) {
        this.id = id;
    }

    // Get the user's name
    public String getName() {
        return name;
    }

    // Set the user's name
    public void setName(String name) {
        this.name = name;
    }

    // Get the user's password
    public String getPassword() {
        return password;
    }

    // Set the user's password
    public void setPassword(String password) {
        this.password = password;
    }

    // Get the user's balance
    public double getBalance() {
        return balance;
    }

    // Set the user's balance
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Add an amount to the user's balance
    public void updateBalance(double reward) {
        this.balance += reward;
    }

    // Get the user's inventory
    public Inventory getInventory() {
        return inventory;
    }

    // Set the user's inventory
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Display user details as a string
    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Balance: %.2f", id, name, balance);
    }

    // Placeholder for setting user ID
    public void getId(int userid) {
        // Overloades the other getId(); method.
    }
}