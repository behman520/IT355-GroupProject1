//this represents a basic user in the shopping app, with certain functions they are allowed to perform 
//ignore the package error if its giving one

import java.util.List;
import java.util.Scanner;



/**
 * Represents a signed-in (or newly registered) user of the shopping app.
 * <p>
 * A {@code User} stores the account's basic profile information and owns the
 * user's {@link ShoppingCart}. It also contains the console-driven menus the
 * user interacts with: signing up or logging in, browsing items, managing the
 * cart, managing payment methods, and checking out. All persistent data is
 * read from and written to the database through {@link Controller}.
 * </p>
 * <p>
 * This class is {@code final} and cannot be subclassed.
 * </p>
 */
public final class User {
    /** The user's current shopping cart. Not persisted unless saved elsewhere. */
    private ShoppingCart cart;
    /** The database ID assigned to the user. */
    private int userID; 
    /** the persons name (just first name, to keep more information private). */
    private String name; 
    /** this is the users id. This is not shown to the user, but is used when pulling from the database. */
    private String username;  
    /** this is the users address that they selected. */
    private String address;  

    /**
     * Constructs a new {@code User} with an empty shopping cart.
     * <p>
     * The user ID is not set by this constructor; use {@link #setID(int)} once
     * the ID is known (for example, after the user is added to the database).
     * This constructor is package-private, so it can only be called from
     * classes within the same package.
     * </p>
     *
     * @param nme   the user's first name
     * @param uname the user's unique username
     * @param a     the user's address
     */
    User(String nme, String uname, String a) {
        this.name = nme;
        this.username = uname;
        this.address = a;
        this.cart = new ShoppingCart();
    }

    /**
     * Prompts the user at the terminal to either sign up for a new account or
     * log in to an existing one.
     * <p>
     * The user is asked to enter {@code "sign up"} or {@code "log in"}
     * and is re-prompted until one of them is entered.
     * </p>
     * <ul>
     *   <li><b>Sign up:</b> asks for a first name, a username (re-prompting
     *       until one that is not already taken is entered), an address and a
     *       password. The account is then stored in the database.</li>
     *   <li><b>Log in:</b> asks for a username and password and checks them
     *       against the database. On success, the user's name, address and ID
     *       are loaded.</li>
     * </ul>
     * <p>
     * Passwords are read with {@link java.io.Console#readPassword()} so they
     * are not echoed to the screen. Because of this, the method requires the
     * program to be run from a real console; {@code System.console()} returns
     * {@code null} in many IDEs, which would cause a
     * {@link NullPointerException}.
     * </p>
     *
     * @return the signed-in or newly created {@code User}, or {@code null} if
     *         sign-up failed (the account could not be added to the database)
     *         or the login credentials were incorrect
     */
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
                System.out.print("Please choose a different username: "); // ask for a username ERR01-J: Do not allow exceptions to expose sensitive information. 
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
            } else // get the user's id and set it because we know the user was added if we get to
                   // the else
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
                signedInUser.setID(Controller.getUserID(username)); // set the user's id based on their username
            } else
                signedInUser = null; // something went wrong with the sign in

            return signedInUser;
        }
    }

    /**
     * Prints the contents of the user's shopping cart to standard output.
     * <p>
     * Each item is shown with its name, unit price and the quantity in the
     * cart. If the cart is empty, a message saying so is printed instead.
     * </p>
     */
    public void viewShoppingCart() {

        System.out.println();
        if (cart.isEmpty()) { // if the cart is empty, let the user know{
            System.out.println("SHOPPING CART----------------");
            System.out.println("No items in your cart");
            return;
        }
        System.out.println("SHOPPING CART----------------");
        for (Item e : cart.getList()) // display all items in the cart
        {
            System.out.println(e.getName() + " - $" + e.getPrice() + " each (" + e.getAmount() + " in cart)");
        }
    }

    /**
     * Prompts the user for an item name and adds one unit of it to their
     * shopping cart.
     * <p>
     * The item name is case-sensitive. The item is not added, and an
     * explanatory message is printed instead, if no item with that name
     * exists or if the quantity already in the cart has reached the item's
     * stock level. The price is looked up from the database when the item is
     * added.
     * </p>
     * <p>
     * The cart only lasts while the application is open.
     * </p>
     */
    public void addItemToShoppingCart() {

        Scanner scan = new Scanner(System.in);

        String itemName = "";
        System.out.print("Enter the name of the item (letters are case sensitive): ");

        itemName = scan.nextLine();

        boolean inListOfItems = Controller.checkIfItemIsInList(itemName);

        if (inListOfItems == false) // if that item doesn't exist exit the function
        {
            System.out.println("\nCouldn't find that item, check spelling.\n"); // ERR01-J: Do not allow exceptions to expose sensitive information. 
            return;
        }

        double price = Controller.returnItemPrice(itemName); // get the price of the item

        Integer amountInDB = Controller.getStockAmount(itemName); // gets the stock amount of the item in the db

        System.out.println("amount in db: " + amountInDB);

        int inCart = cart.getAmountOf(itemName);

 
         if (inCart >= amountInDB) // if the item is already in the cart increment amount in cart by 1
        {
            System.out.println("\nReached stock limit of " + itemName + "\n");
            return;
        }

        cart.addOne(itemName, price);
        System.out.println(
                "\nItem added to shopping cart! Your cart will persist while keeping application open (add cart to orders to keep items persistent)\n");
    }

    /**
     * Prompts the user for an item name and removes one unit of it from their
     * shopping cart.
     * <p>
     * The item name is case-sensitive. A message is printed telling the user
     * whether a unit was removed or the item could not be found in the cart.
     * </p>
     */
    public void removeItemFromShoppingCart() // remove the specified item from the shopping cart
    {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter the item to remove (by name, case-sensitive): ");
        String remove = scan.nextLine();

        boolean wasRemoved = cart.removeOne(remove); // determines whether the item was removed or not

        if (wasRemoved) // if the item was removed print out that it was, otherwise print that it wasn't
            System.out.println("\nSucessfully removed 1 \"" + remove + "\" from the cart");
        else
            System.out.println("\nCouldn't remove \"" + remove + "\" from cart (maybe check spelling)"); // ERR01-J: Do not allow exceptions to expose sensitive information.

    }

        /**
     * Displays the payment methods menu, which lets the user add a card or
     * view the cards on file.
     * <p>
     * The menu loops until the user enters {@code x}:
     * </p>
     * <ul>
     *   <li>{@code a} - prompts for the card holder name, the last four digits
     *       and the expiration date, then saves the card to the user's
     *       account</li>
     *   <li>{@code b} - prints the cards currently on file</li>
     *   <li>{@code x} - returns to the previous menu</li>
     * </ul>
     * <p>
     * Input is case-insensitive. Only the last four digits of the card number
     * are collected and stored.
     * </p>
     */
    public void viewPaymentMethods() // shows the cards the user has on file and allows them to add a card
    {
        Scanner scan = new Scanner(System.in);

        System.out.println("\nVIEW PAYMENT METHODS ------------------");

        String input = "";

        System.out.println("(A) Add payment method\n(B) Display payment methods\n(X) Go back");

        System.out.print("Enter choice: ");

        input = scan.nextLine().toLowerCase();

        while (!input.equals("x")) {
            if (input.equals("a")) {
                // get the card information
                String card_holder = "";
                String last_four = "";
                String expiry = "";
                System.out.print("Enter the card holder name: ");
                card_holder = scan.nextLine();
                System.out.print("Enter the the last 4 digits: ");
                last_four = scan.nextLine();
                System.out.print("Enter the expiration data: ");
                expiry = scan.nextLine();

                Card newCard = new Card(card_holder, last_four, expiry);

                Controller.addUserCard(newCard, this); // add the card for the user

            } else if (input.equals("b")) {
                System.out.println();
                Controller.displayUserCardsOnFile(this.getUsername());
                System.out.println();
            }

            System.out.println("(A) Add payment method\n(X) Go back");

            System.out.print("Enter choice: ");

            input = scan.nextLine().toLowerCase();

        }
    }

    /**
     * Displays the item browsing menu, which lets the user view the items for
     * sale and add them to their shopping cart.
     * <p>
     * The list of items is printed when the menu opens. The menu then loops
     * until the user enters {@code x}:
     * </p>
     * <ul>
     *   <li>{@code a} - prints the list of items for sale again</li>
     *   <li>{@code b} - prompts for an item and adds it to the cart (see
     *       {@link #addItemToShoppingCart()})</li>
     *   <li>{@code x} - returns to the previous menu</li>
     * </ul>
     */
    public void browseItemSelection() { // allows a signed in user to browse items and add to shopping cart
        Scanner scan = new Scanner(System.in);

        String input = "";

        System.out.println("\nItems for sale:");
        Controller.displayListOfItems();

        System.out.println("\nBROWSE ITEM SELECTION ------------------");
        System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(X) Go back");

        System.out.print("Enter choice: ");

        input = scan.nextLine();
        input = input.toLowerCase();

        while (!input.equals("x")) {
            switch (input) {
                case "a":
                    Controller.displayListOfItems();
                    break;

                case "b":
                    this.addItemToShoppingCart(); // adds the item to the users shopping cart
                    break;
            }

            // reprompt the menu for browsing items
            System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(X) Go back");
            System.out.print("Enter choice: ");
            input = scan.nextLine();
        }
    }

    /**
     * Displays the checkout menu, which lets the user adjust their cart and
     * begin paying for their order.
     * <p>
     * The menu loops until the user enters {@code x}:
     * </p>
     * <ul>
     *   <li>{@code a} - removes one unit of an item from the cart and then
     *       shows the updated cart</li>
     *   <li>{@code b} - shows the shipping address and the cart subtotal, then
     *       checks for a card on file. If the user has a card, they are asked
     *       which one to use by its last four digits; otherwise they are told
     *       to add one under "View payment methods"</li>
     *   <li>{@code x} - returns to the previous menu</li>
     * </ul>
     * <p>
     * Note: payment is not yet processed. The card choice is read but not
     * used, and the order is not saved or the cart cleared.
     * </p>
     */
    public void checkOut() {
        Scanner scan = new Scanner(System.in);

        System.out.print("\nCHECKOUT ------------------");
        System.out.println("\n(A) Decrease count of item in cart\n(B) Pay and finish order\n(X) Go back");
        System.out.print("Enter choice: ");
        String input = "";

        input = scan.nextLine().toLowerCase();

        while (!input.equals("x")) {
            switch (input) {
                case "a":
                    this.removeItemFromShoppingCart();
                    this.viewShoppingCart(); // let the user see the new shopping cart
                    break;

                case "b":
                    System.out.println("\nAddress shipped to: " + this.getAddress());

                    // get the subtotal of all items in the shopping cart
                    double subtotal = cart.getSubtotal();
                    System.out.println("Subtotal: $" + subtotal);

                    // check if user has a card on file
                    boolean cardOnFile = Controller.hasCardOnFile(this.getUsername());
                    if (cardOnFile) // if the user has a card on file allow them to choose which one they want to
                                    // use
                    {
                        System.out.print("Which card would you like to use? Enter the last 4 digits:");
                        String last4Digits = scan.nextLine();

                    } else
                        System.out.println("You have no cards on file. Go to \"View payment methods\" to add a card");
                    break;
            }
            // get next input
            System.out.println("\n(A)Decrease count of item in cart\n(B) Pay and finish order\n(X) Go back");
            System.out.print("Enter choice: ");
            input = scan.nextLine().toLowerCase();
        }
    }

    /**
     * Returns the user's database ID.
     *
     * @return the user's ID, or {@code 0} if it has not been set
     */
    public int getID() {
        return this.userID;
    }

    /**
     * Sets the user's database ID.
     *
     * @param id the ID assigned to the user in the database
     */
    public void setID(int id) {
        this.userID = id;
    }

    /**
     * Returns the user's first name.
     *
     * @return the user's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the user's first name.
     * <p>
     * This only changes the name on this {@code User} object; it does not
     * update the database.
     * </p>
     *
     * @param n the new name
     */
    public void setName(String n) {
        this.name = n;
    }

    /**
     * Returns the user's address.
     *
     * @return the user's address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Sets the user's address.
     * <p>
     * This only changes the address on this {@code User} object; it does not
     * update the database.
     * </p>
     *
     * @param a the new address
     */
    public void setAddress(String a) {
        this.address = a;
    }

    /**
     * Returns the user's username, which is used to look the user up in the
     * database.
     *
     * @return the user's username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns a read-only snapshot of the items in the user's shopping cart.
     * <p>
     * The returned items are copies, so changing them does not affect the
     * cart. See {@link ShoppingCart#getList()}.
     * </p>
     *
     * @return an unmodifiable list of copies of the cart's items
     */
    public List<Item> getCartItems() {
        return cart.getList();
    }

    /**
     * Adds an item to the user's cart in the given quantity, for example when
     * restoring a previously saved cart.
     * <p>
     * If the item is already in the cart, its quantity is increased by
     * {@code amount}. An {@code amount} of 0 leaves the cart unchanged.
     * </p>
     *
     * @param name   the name of the item; must not be {@code null} or blank
     * @param price  the price of a single unit; must not be negative or NaN
     * @param amount the number of units to add; must not be negative
     * @throws IllegalArgumentException if {@code name} is {@code null} or
     *                                  blank, {@code price} is negative or
     *                                  NaN, or {@code amount} is negative
     */
    public void restoreCartItem(String name, double price, int amount) {
        if (name == null || name.isBlank() || Double.isNaN(price) || price < 0 || amount < 0) {
            throw new IllegalArgumentException("invalid cart item");
        }
            for (int i = 0; i < amount; i++) {
                cart.addOne(name, price);
            }
        }

}
