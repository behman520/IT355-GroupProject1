/**
 * Represents an item that a user can add to their shopping cart.
 * <p>
 * An {@code Item} holds the item's name and unit price, along with the
 * quantity the user currently intends to buy. The name and price are fixed
 * when the item is created; only the quantity can change.
 * </p>
 */
public class Item {
    /** The name of the item. */
    private String name; 

    /** price of item. */
    private double price; 

    /** number of items a person is buying of this item */     
    private int amount;  

    /**
     * Constructs a new {@code Item} with the given name and price.
     * The purchase amount starts at 0.
     * <p>
     * This constructor is package-private, so it can only be called from
     * classes within the same package.
     * </p>
     *
     * @param n     the name of the item
     * @param price the price of a single unit of the item
     */
    Item(String n, double price) {
        this.name = n;
        this.amount = 0;
        this.price = price;
    }

    /**
     * Constructs a copy of an existing {@code Item}, including its name,
     * price and current purchase amount.
     * <p>
     * This constructor is package-private, so it can only be called from
     * classes within the same package.
     * </p>
     *
     * @param other the item to copy
     */
    Item(Item other) {
        this.name = other.name;
        this.amount = other.amount;
        this.price = other.price;
    }

    /**
     * Increases the amount of this item being bought by one.
     */
    public void incrementAmount() // increments the amount of an item being bought by one
    {
        this.amount++;
    }

    /**
     * Decreases the amount of this item being bought by one.
     * <p>
     * No check is made to keep the amount from going below zero, so callers
     * should verify the amount is greater than zero first.
     * </p>
     */
    public void decrementAmount() // decrements the amount of an item being bought by one
    {
        this.amount--;
    }

    /**
     * Returns the name of the item.
     *
     * @return the item's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the price of a single unit of the item.
     *
     * @return the item's unit price
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Returns the number of units of this item being bought.
     *
     * @return the current purchase amount
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets the number of units of this item being bought.
     *
     * @param a the new purchase amount
     */
    public void setAmount(int a) {
        this.amount = a;
    }

}
