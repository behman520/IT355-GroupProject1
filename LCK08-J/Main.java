import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.locks.*;

public class Main {
    
    static Lock lock = new ReentrantLock();
    
    public static void main(String [] args)
    {
        InputStream fileRead = null;
        File file = new File("");

        lock.lock();

        try{

            fileRead = new FileInputStream(file);
            Scanner scan = new Scanner(fileRead);

            while(scan.hasNextLine())
            {
                System.out.println(scan.nextLine());
            }

            System.out.println("Here");
            lock.unlock();

             
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Reached here");
        }
        finally
        {
            
        }
    }

}
