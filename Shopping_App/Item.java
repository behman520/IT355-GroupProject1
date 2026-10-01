//this represents an Item the user can add to their shopping cart


public class Item {
    private String itemID; //the unique identifer for the item
    private String name; //the name of the item
    private int amount; //number of items a person is buying of this item (might need to be moved to another class)


    Item(String n)
    {   
        this.name = n;
        this.amount = 0;
        this.itemID = generateID(); //generate a random unique id for the item

    }

    public void incrementAmount() //increments the amount of an item being bought by one
    {
        this.amount++;
    }

    public void decrementAmount() //decrements the amount of an item being bought by one
    {
        this.amount--;
    }

    public String getName()
    {
        return this.name;
    }

    private String generateID() //generates a random unique id for the item when the item is created
    {
        return "some_randomly_generated_id";
    }

    public String getItemID()
    {
        return this.itemID;
    }

    public int getAmount()
    {
        return this.amount;
    }
    

}
