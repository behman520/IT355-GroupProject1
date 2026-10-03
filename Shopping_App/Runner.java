import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter; //for writing to the list of user's file

public class Runner {

    static File listOfUsers; //this represents the text file containing users
    static Scanner scan = new Scanner(System.in);
    
    public static User Account(File f) //sign in or log in the user
    {
        String enteredNumber; //the number the users enters in. Representing as a string because nextInt sucks
        String usersName = ""; //represents the user's entered name (not username)

        System.out.println("Hello. Press 1 to login and press 2 to create an account, then press 'Enter' key");

        enteredNumber = scan.nextLine();

        if(enteredNumber.equals("1")) //login
        {
            System.out.print("What is your name: ");
            usersName = scan.nextLine();

            //log in the user by their first name. obviously not secure at all, if we have time a backend sql server would be nice 
            try(Scanner fileReader = new Scanner(f))
            {
                String line = ""; // a line in the file

                while(fileReader.hasNextLine())
                {
                    line = fileReader.nextLine();

                   
                }
            }
            catch(FileNotFoundException e)
            {
                System.out.println("Couldn't find list of users, exiting.");
                System.exit(0);
            }

        }
        else if(enteredNumber.equals("2")) //create an account
        {
            String usersAddress = "";           

            System.out.print("Enter name: ");
            usersName = scan.nextLine();

            System.out.print("Enter address: ");
            usersAddress =  scan.nextLine();

            User newUser = new User(usersName, usersAddress);
             
            //add user to the list

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
            
        String input  = ""; //choice user selects for the switch statement
        
        listOfUsers = new File("listOfUsers.txt"); //open the text file containing the users

        User currentUser = Account(listOfUsers); //get the user using the system. Pass the list of users file to the function

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
