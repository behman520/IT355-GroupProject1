import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.locks.*;

/**
 * Demonstrates a resource-leak bug involving a Lock that is not
 * released when an exception occurs.
 * If a Lock is aquired, but an exception occurs midway through, the lock will never be 
 * released and any other resources that try to access the lock will deadlock forever
 * The way around this is to access the lock outside of the try block and only unlock 
 * once outside of the try block, in the finally block since the finally block will alays execute
 */
public class Main {
    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        InputStream fileRead = null;
        File file = new File("");
        lock.lock();
        try {
            fileRead = new FileInputStream(file);
            Scanner scan = new Scanner(fileRead);
            while (scan.hasNextLine()) {
                System.out.println(scan.nextLine());
            }
            System.out.println("Here");
             
        } catch (FileNotFoundException e) {
            System.out.println("Reached here");
        } finally {

            lock.unlock();
        }
    }
}