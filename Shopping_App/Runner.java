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
                "(A) Browse item selection  (displays list of items, and allow you to add items to shopping cart)\n(B) View shopping cart  (view the items and amount in your shopping cart)\n(C) Checkout  (Confirm items in your shopping cart and orders them)\n(D) Preview current and past orders\n(E) View payment methods (see cards on file and add cards)\n(X) Exit application");
        System.out.print("Enter choice: ");
    }

    public static void main(String[] args) {

        Controller.createTables(); // create the database tables, if not already created
        Controller.addItems(); //creates the items in the Items table, if not already created

        User signedInUser = User.userSignIn(); // sign the user in or have them create an account. 

        while (signedInUser == null) // something went wrong during sign in, reprompt them to log in
        {
            System.out.println("\nSign in/log in failed, please try again");
            signedInUser = User.userSignIn(); // sign the user in or have them create an account
        }

        String input = ""; // choice user selects for the switch statement

        menu(Controller.getName(signedInUser.getUsername())); // prompt the user with the first menu
        input = scan.nextLine();

        input = input.toLowerCase();

        while (!input.equals("x")) // continue looping until the user wants to quit (Enters 'X')
        {
            switch (input) {
                case "a":

                    browseItemSelection(signedInUser); //allow the user to browse items on the site and add items to their shopping cart

                break;

                case "b":

                    signedInUser.viewShoppingCart();//lets the user see whats in their shopping cart
                     
                break;

                case "c":
                    signedInUser.viewShoppingCart(); //first let the user see what is in their shopping cart

                    //then let the user check out
                    checkoutUser(signedInUser);
 
                break;

                case "d":

 
                break;

                case "e":

                signedInUser.viewPaymentMethods(); //presents the cards the user has on file
                
                break;
            }

            menu(signedInUser.getName());
            input = scan.nextLine(); // get next thing to do
        }
    } //end of main
 
    public static void browseItemSelection(User signedInUser){ //allows a signed in user to browse items and add to shopping cart
        
        String input = "";

        System.out.println("\nItems for sale:");
        Controller.displayListOfItems();

        System.out.println("\nBROWSE ITEM SELECTION ------------------");
        System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(X) Go back");

        System.out.print("Enter choice: ");

        input = scan.nextLine();
        input = input.toLowerCase();

        while(!input.equals("x"))
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

            System.out.println("(A) Display list of items\n(B) Add item to shopping cart\n(X) Go back");

            System.out.print("Enter choice: ");

            input = scan.nextLine();
            
        }    
    }

    public static void checkoutUser(User signedInUser)
    {
        System.out.print("\nCHECKOUT ------------------");
        System.out.println("\n(A) Decrease count of item in cart\n(B) Pay and finish order\n(X) Go back");
        System.out.print("Enter choice: ");
        String input = "";

        input = scan.nextLine().toLowerCase();

        while(!input.equals("x"))
        {
            switch(input)
            {
                case "a":
                    signedInUser.removeItemFromShoppingCart();
                    signedInUser.viewShoppingCart(); //let the user see the new shopping cart
                break;

                case "b":
                    System.out.println("\nAddress shipped to: " + signedInUser.getAddress());

                    //get the subtotal of all items in the shopping cart
                    double subtotal = 0.0;
                    for(Item i : signedInUser.getCart().getList())
                    {
                        subtotal += i.getPrice() * i.getAmount(); //multiply by the amount of the item
                    }
                    System.out.println("Subtotal: $" + subtotal);

                    //check if user has a card on file
                    boolean cardOnFile = Controller.hasCardOnFile(signedInUser.getUsername());
                    if(cardOnFile) //if the user has a card on file allow them to choose which one they want to use
                    {
                        System.out.print("Which card would you like to use? Enter the last 4 digits:");
                        String last4Digits = scan.nextLine();

                    }
                    else
                        System.out.println("You have no cards on file. Go to \"View payment methods\" to add a card");
                break;
            }
            //get next input
            System.out.println("\n(A)Decrease count of item in cart\n(B) Pay and finish order\n(X) Go back"); 
            System.out.print("Enter choice: ");
            input = scan.nextLine().toLowerCase();
        }
    }
}
