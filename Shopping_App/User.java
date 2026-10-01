//this represents a basic user in the shopping app, with certain functions they are allowed to perform 
//ignore the package error if its giving one


public class User
{
    private String name; //the persons name (just first name, to keep more information private)
    private String userID; //this is the users id. This is not shown to the user, but is used when pulling from the database
    private String address; //this is the users address that they selected
    private ShoppingCart cart; //the users shopping cart
    private Orders orders; //users past and present orders

    User(String n, String a)
    {
        this.name = n;
        this.address = a;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String n)
    {
        this.name = n;
    }

    public String getAddress()
    {
        return this.address;
    }

    public void setAddress(String a)
    {
        this.address = a;
    }



}