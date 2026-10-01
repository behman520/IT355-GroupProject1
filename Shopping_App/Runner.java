import java.util.ArrayList;
import java.util.Scanner;

public class Runner {

    static ArrayList<User> currentUsers; //a list of the current users using the system
    static Scanner scan = new Scanner(System.in);
    
    public static User login()
    {
 
        String enteredNumber; //the number the users enters in. Representing as a string because nextInt sucks
        String usersName = ""; //represents the user's entered name
         
        System.out.println("Hello. Press 1 to login and press 2 to create an account, then press 'Enter' key");

        enteredNumber = scan.nextLine();

        if(enteredNumber.equals("1"))
        {
            System.out.print("What is your name: ");
            usersName = scan.nextLine();

            for(User u : currentUsers)
            {
                if (u.getName().equals(usersName))
                {
                    return u;
                }
            }
        }
        else if(enteredNumber.equals("2"))
        {
            String usersAddress = "";           

            System.out.print("Enter name: ");
            usersName = scan.nextLine();

            System.out.print("Enter address: ");
            usersAddress =  scan.nextLine();

            User newUser = new User(usersName, usersAddress);
            currentUsers.add(newUser);

            return newUser;
             
        }
        else
        {
            System.out.println("You did not enter 1 or 2, exiting application, try again.");
            System.exit(0);
        }

        return null; //never hits
    }

    public static void menu(String name)
    {
        System.out.println("\nHello " + name + ", what would you like to do?");
        System.out.println("(A) Browse item selection\n(B) View shopping cart\n(C) Preview current and past orders\n(D) View payment methods\n(E) Exit application");
    }
    
    public static void main(String[] args) {
           
        currentUsers = new ArrayList<>();

        String input  = ""; //choice user selects
        
        User currentUser = login(); //get the user using the system.
        menu(currentUser.getName());

        input = scan.nextLine(); //get the first user input

        while(!input.toUpperCase().equals("E"))
        {
            System.out.println(input);

            switch(input)
            {
                case "A":
                case "a":

                browseItemSelection(currentUser);

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

            input = scan.nextLine(); //get next thing to do
        }

    }

    public static void browseItemSelection(User u)
    {

    }

}
