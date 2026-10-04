import java.util.Scanner;

public class Runner {

    static Scanner scan = new Scanner(System.in);

    // the main menu for the app
    public static void menu(String name) {
        System.out.println("\nHello " + name + ", what would you like to do?");
        System.out.println(
                "(A) Browse item selection\n(B) View shopping cart\n(C) Preview current and past orders\n(D) View payment methods\n(E) Exit application");
        System.out.print("Enter selection: ");
    }

    public static void main(String[] args) {

        Controller.createTables(); // create the database tables, if not already created

        User signedInUser = userSignIn(); // sign the user in or have them create an account

        while (signedInUser == null) // something went wrong during sign in
        {
            System.out.println("Sign in/log in failed, please try again");
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

                    break;

                case "B":
                case "b":

                    // viewShoppingCart(currentUser);

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
        System.out.print("Welcome! Enter 'sign up' or 'log in' to continue: ");

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
            String password;

            System.out.print("Enter your first name or press enter to skip: "); // ask for a username
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
            password = scan.nextLine();

            User newUser = new User(name, username, address);

            boolean userAdded = Controller.addUser(newUser, password);

            if (userAdded == false) // set the new user to null if the database couldn't add the user
            {
                newUser = null;
            }

            return newUser;
        } else { //log in the user
            String username;
            String password;

            System.out.print("Enter your username: ");

            username = scan.nextLine();

            System.out.print("Enter your password: ");

            password = scan.nextLine();

            boolean userExists = Controller.login(username, password); //log in the user and see if their account exists

            User signedInUser; //the user account that is signed in

            if(userExists)
            {
                System.out.println("\nSigned in.");
                signedInUser = new User(Controller.getName(username), username, "/* add address */");
            }
            else
                signedInUser = null; //something went wrong with the sign in

            return signedInUser;

        }
    }
}
