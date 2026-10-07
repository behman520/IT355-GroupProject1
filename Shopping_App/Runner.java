import java.util.Scanner;

public class Runner {

    static Scanner scan = new Scanner(System.in);

    // the main menu for the app
    public static void menu(String name) {
        if (name.equals("n") || name.equals("\'n\'"))
            System.out.println("\nHello, what would you like to do?");
        else
            System.out.println("\nHello " + name + ", what would you like to do?");

        System.out.println(
                "(A) Browse item selection  (displays list of items, and allow you to add items to shopping cart)\n(B) View shopping cart  (view the items and amount in your shopping cart)\n(C) Checkout  (Confirm items in your shopping cart and orders them)\n(D) Preview current and past orders\n(E) View payment methods (see cards on file and add cards)\n(X) Exit application");
        System.out.print("Enter choice: ");
    }

    public static void main(String[] args) {

        Controller.createTables(); // create the database tables, if not already created
        Controller.addItems(); // creates the items in the Items table, if not already created

        User signedInUser = User.userSignIn(); // sign the user in or have them create an account.

        while (signedInUser == null) // something went wrong during sign in, reprompt them to log in
        {
            System.out.println("\nSign in/log in failed, please try again"); // ERR01-J: Do not allow exceptions to expose sensitive information. 
            signedInUser = User.userSignIn(); // sign the user in or have them create an account
        }

        String input = ""; // choice user selects for the switch statement

        menu(signedInUser.getName()); // prompt the user with the first menu
        input = scan.nextLine();

        input = input.toLowerCase();

        while (!input.equals("x")) // continue looping until the user wants to quit (Enters 'X')
        {
            switch (input) {
                case "a": // (A) Browse item selection (displays list of items, and allow you to add items
                          // to shopping cart)
                    signedInUser.browseItemSelection(); // allow the user to browse items on the site and add items to
                                                        // their shopping cart
                    break;

                case "b": // (B) View shopping cart (view the items and amount in your shopping cart)
                    signedInUser.viewShoppingCart();// lets the user see whats in their shopping cart
                    break;

                case "c": // (C) Checkout (Confirm items in your shopping cart and orders them)
                    signedInUser.viewShoppingCart(); // first let the user see what is in their shopping cart

                    // then let the user check out
                    signedInUser.checkOut();

                    break;

                case "d": // (D) Preview current and past orders
                    // NOTHING HERE YET
                    break;

                case "e": // (E) View payment methods (see cards on file and add cards)
                    signedInUser.viewPaymentMethods(); // presents the cards the user has on file
                    break;
            }

            menu(signedInUser.getName());
            input = scan.nextLine(); // get next thing to do
        }
    } // end of main
}
