//this represents a basic user in the shopping app, with certain functions they are allowed to perform 
//ignore the package error if its giving one

import java.util.Scanner;

public final class User {
    private ShoppingCart cart;
    private int userID; // the id assigned to the user
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

    // allows a user to sign into their account or sign up for an account
    //returns the signed in User at the end
    public static User userSignIn() {
        Scanner scan = new Scanner(System.in); // scanner to read information from the user

        System.out.print("\nWelcome! Enter 'sign up' or 'log in' to continue: "); // greet the user

        String choice = scan.nextLine(); // represents the choice to sign up or log in
        choice = choice.toLowerCase();

        // user didn't enter 'sign up' or 'log in', keep prompting them for it
        while (!(choice.equals("sign up") || choice.equals("log in"))) {
            System.out.print("Please enter key phrase 'sign up' or 'log in' to continue: ");
            choice = scan.nextLine();
            choice = choice.toLowerCase();
        }

        if (choice.equals("sign up")) // deal with sign up
        {
            String name;
            String username;
            String address;
            char[] password;

            System.out.print("Enter your first name or type 'n' to skip: "); // ask for a username
            name = scan.nextLine();

            System.out.print("Enter a username: "); // ask for a username
            username = scan.nextLine();

            // check if that username already exists
            boolean exists = Controller.userExists(username);

            while (exists) {
                System.out.print("Please choose a different username: "); // ask for a username
                username = scan.nextLine();

                // check if that username already exists
                exists = Controller.userExists(username);
            }

            System.out.print("Enter an address: "); // ask for a username
            address = scan.nextLine();

            System.out.print("Enter an password: "); // ask for a username
            password = System.console().readPassword(); // reads a password and doesn't display the text to the console

            String strPassword = "";

            for (char c : password) {
                strPassword += c;
            }
            User newUser = new User(name, username, address);

            boolean userAdded = Controller.addUser(newUser, strPassword);

            if (userAdded == false) // set the new user to null if the database couldn't add the user
            {
                newUser = null;
            } else // get the user's id and set it because we know the user was added if we get to the else
            {
                newUser.setID(Controller.getUserID(username));
            }

            return newUser;

        } else { // log in the user
            String username;
            char[] password;

            System.out.print("Enter your username: ");

            username = scan.nextLine();

            System.out.print("Enter your password (it will not display on screen): ");

            password = System.console().readPassword(); // get the password but don't display it to the terminal

            String strPassword = "";

            for (char c : password) {
                strPassword += c;
            }

            boolean userExists = Controller.login(username, strPassword); // log in the user and see if their account
                                                                          // exists

            User signedInUser; // the user account that is signed in

            if (userExists) {
                System.out.println("\nSigned in.");
                signedInUser = new User(Controller.getName(username), username, Controller.getAddress(username));
                signedInUser.setID(Controller.getUserID(username)); //set the user's id based on their username
            } else
                signedInUser = null; // something went wrong with the sign in

            return signedInUser;
        }
    }

    public void viewShoppingCart()
    {
        
        System.out.println();
        if(this.getCart().getList().size() == 0)
        {
            System.out.println("SHOPPING CART----------------");
            System.out.println("No items in your cart");
            return;
        }
        System.out.println("SHOPPING CART----------------");
        for(Item e : this.getCart().getList()) //display all items in the cart
        {
            System.out.println(e.getName() + " - $" + e.getPrice() + " each (" + e.getAmount() + " in cart)");
        }
    }

    public int getID() {
        return this.userID;
    }

    public void setID(int id) {
        this.userID = id;
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

    public ShoppingCart getCart() {
        return this.cart;
    }

    

    // EVERYTHING BELOW HERE NEEDS TO BE MOVED TO A DIFFERENT CLASS



    // MOVE TO SHOPPING CART CLASS
    public boolean addItemToShoppingCart(String itemName) // adds an item to the users shopping cart
    {

        double price = Controller.returnItemPrice(itemName);

        boolean inListOfItems = Controller.checkIfItemIsInList(itemName);

        if (inListOfItems == false)
            return false;

        Item newItem = new Item(itemName, price);

        if (cart.containsItem(newItem.getName())) // if the item is already in the cart increment amount by 1
        {
            for (Item e : cart.getList()) {
                if (e.getName().equals(itemName)) {
                    e.incrementAmount();
                    return true;
                }
            }

            return true;
        } else { // if item is not already in the cart, set its amount to 1

            cart.getList().add(newItem);
            newItem.setAmount(1);
        }

        return true;
    }
}