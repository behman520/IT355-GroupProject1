import java.util.Scanner;

public class Runner {

    static Scanner scan = new Scanner(System.in);

    // the main menu for the app
    public static void menu(String name) {
        if(name.equals("n") || name.equals("\'n\'"))
            System.out.println("\nHello, what would you like to do?");
        else
            System.out.println("\nHello " + name + ", what would you like to do?");

        System.out.println(
                "(A) Browse item selection\n(B) View shopping cart\n(C) Preview current and past orders\n(D) View payment methods\n(E) Exit application");
        System.out.print("Enter choice: ");
    }

    public static void main(String[] args) {

        Controller.createTables(); // create the database tables, if not already created
        Controller.addItems(); //creates the items in the Items table, if not already created

        User signedInUser = userSignIn(); // sign the user in or have them create an account

        while (signedInUser == null) // something went wrong during sign in
        {
            System.out.println("\nSign in/log in failed, please try again");
            signedInUser = userSignIn(); // sign the user in or have them create an account
        }

        String input = ""; // choice user selects for the switch statement

        menu(Controller.getName(signedInUser.getUsername())); // prompt the user with the first menu
        input = scan.nextLine();

        while (!input.toUpperCase().equals("E")) // continue looping until the user wants to quit (Enters 'E')
        {
            switch (input) {
                case "A":
                case "a":

                    browseItemSelection(signedInUser); //allow the user to browse items on the site and add items to their shopping cart

                break;

                case "B":
                case "b":

                    viewShoppingCart(signedInUser); //lets the user see whats in their shopping cart

                break;

                case "C":
                case "c":

                    // previewOrders(currentUser);

                    break;

                case "D":
                case "d":

                    // viewPaymentMethods(currentUser);

                    break;
            }

            menu(signedInUser.getName());
            input = scan.nextLine(); // get next thing to do
        }
    } //end of main

    public static User userSignIn() {
        System.out.print("\nWelcome! Enter 'sign up' or 'log in' to continue: ");

        String choice = scan.nextLine();
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
            password = System.console().readPassword(); //reads a password and doesn't display the text to the console

            String strPassword = "";

            for(char c : password)
            {
                strPassword += c;
            }
            User newUser = new User(name, username, address);

            boolean userAdded = Controller.addUser(newUser,strPassword);

            if (userAdded == false) // set the new user to null if the database couldn't add the user
            {
                newUser = null;
            }

            return newUser;
        } else { //log in the user
            String username;
            char[] password;

            System.out.print("Enter your username: ");

            username = scan.nextLine();

            System.out.print("Enter your password (it will not display on screen): ");

            password = System.console().readPassword(); //get the password but don't display it to the terminal

            String strPassword = "";

            for(char c : password)
            {
                strPassword += c;
            }

            boolean userExists = Controller.login(username, strPassword); //log in the user and see if their account exists

            User signedInUser; //the user account that is signed in

            if(userExists)
            {
                System.out.println("\nSigned in.");
                signedInUser = new User(Controller.getName(username), username, Controller.getAddress(username));
            }
            else
                signedInUser = null; //something went wrong with the sign in

            return signedInUser;

        }
    }
 
    public static void browseItemSelection(User signedInUser){ //allows a signed in user to browse items and add to shopping cart
        
        String input = "";

        System.out.println("\nItems for sale:");
        Controller.displayListOfItems();

        System.out.println("\nBROWSE ITEM SELECTION ------------------");
        System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(E) Go back");

        System.out.print("Enter choice: ");

        input = scan.nextLine();
        input = input.toLowerCase();

        while(!input.equals("e"))
        {

            switch(input)
            {
                case "a":
                    Controller.displayListOfItems();
                break;

                case "b":

                    String name = "";
                    System.out.print("Enter the name of the item(letters are case sensitive): ");

                    name = scan.nextLine();

                    boolean added = signedInUser.addItemToShoppingCart(name); //adds the item to the users shopping cart

                    if(added)
                        System.out.println("\nItem added to shopping cart! Your cart will persist while keeping application open (add cart to orders to keep items persistent)\n");
                    else
                        System.out.println("\nCouldn't add item\n");
                break;
            }

            System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(E) Go back");

            System.out.print("Enter choice: ");

            input = scan.nextLine();
            
        }    
    }

    public static void viewShoppingCart(User signedInUser)
    {
        System.out.println();

        if(signedInUser.getCart().getList().size() == 0)
        {
            System.out.println("No items in your cart");
            return;
        }
        System.out.println("\nSHOPPING CART----------------");
        for(Item e : signedInUser.getCart().getList()) //display all items in the cart
        {
            System.out.println(e.getName() + "-$" + e.getPrice() + " (" + e.getAmount() + " in cart)");
        }
    }

}
