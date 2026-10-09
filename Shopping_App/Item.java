//this represents an Item the user can add to their shopping cart

public class Item {
    private String name; // the name of the item
    private double price; // price of item
    private int amount; // number of items a person is buying of this item (might need to be moved to
                        // another class)

    Item(String n, double price) {
        this.name = n;
        this.amount = 0;
        this.price = price;
    }

    Item(Item other) {
        this.name = other.name;
        this.amount = other.amount;
        this.price = other.price;
    }

    public void incrementAmount() // increments the amount of an item being bought by one
    {
        this.amount++;
    }

    public void decrementAmount() // decrements the amount of an item being bought by one
    {
        this.amount--;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int a) {
        this.amount = a;
    }

}
