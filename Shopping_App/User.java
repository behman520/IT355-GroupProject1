//this represents a basic user in the shopping app, with certain functions they are allowed to perform 
//ignore the package error if its giving one

public final class User {
    private ShoppingCart cart;
    private String name; // the persons name (just first name, to keep more information private)
    private String username; // this is the users id. This is not shown to the user, but is used when pulling
                             // from the database
    private String address; // this is the users address that they selected

    User(String nme, String uname, String a) {
        this.name = nme;
        this.username = uname;
        this.address = a;
        this.cart = new ShoppingCart();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String a) {
        this.address = a;
    }

    public String getUsername() {
        return this.username;
    }

    public ShoppingCart getCart()
    {
        return this.cart;
    }

    public boolean addItemToShoppingCart(String itemName) //adds an item to the users shopping cart
    {

        double price = Controller.returnItemPrice(itemName);

        boolean inListOfItems = Controller.checkIfItemIsInList(itemName);

        if(inListOfItems == false)
            return false;

        Item newItem = new Item(itemName, price);

        if(cart.containsItem(newItem.getName())) //if the item is already in the cart increment amount by 1
        {
            for(Item e : cart.getList())
            {
                if(e.getName().equals(itemName))
                {
                    e.incrementAmount();
                    return true;
                }
            }

            return true;
        }
        else { //if item is not already in the cart, set its amount to 1

            cart.getList().add(newItem);
            newItem.setAmount(1);
        }
        
        return true;
    }
}