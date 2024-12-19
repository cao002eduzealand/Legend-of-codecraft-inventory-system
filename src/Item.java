public class Item {

    // Attributes of the Item class
    private int id;            // Unique identifier for the item
    private String name;       // Name of the item
    private double weight;     // Weight of the item

    // Enumeration for item types (defines the type of an item)
    public enum Type {
        OFF_HAND,  // Items equipped in the off-hand
        ONE_HAND,  // One-handed items
        TWO_HAND   // Two-handed items
    }

    // Enumeration for item rarities (defines the rarity of an item)
    public enum Rarity {
        COMMON,      // Most basic rarity
        UNCOMMON,    // Slightly better than common
        RARE,        // Rare items with higher value
        EPIC,        // Powerful items with significant value
        LEGENDARY    // Extremely rare and valuable items
    }

    // Protected attributes for item type and rarity
    protected Type type;       // Type of the item
    protected Rarity rarity;   // Rarity of the item


    public Item(int id, String name, double weight, Type type, Rarity rarity) {
        this.id = id;           // Assign ID
        this.name = name;       // Assign name
        this.weight = weight;   // Assign weight
        this.type = type;       // Assign type
        this.rarity = rarity;   // Assign rarity
    }

    // Getter and Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Getter and Setter for Name
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Getter and Setter for Weight
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    // Getter and Setter for Type
    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    // Getter and Setter for Rarity
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public Rarity getRarity() {
        return rarity;
    }

    /*
      Overrides the default toString method to provide a string representation of the Item object.
     
      @return A string containing the item's name, weight, type, and rarity.
     */
    @Override
    public String toString() {
        return "Item{name='" + name + "', weight=" + weight + ", type=" + type + ", rarity=" + rarity + "}";
    }
}
