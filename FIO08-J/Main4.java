import java.io.FileInputStream;
import java.io.IOException;

/**
 * Demonstrates a common bug when reading bytes from a binary file using
 *  FileInputStream. FileInputStream.read() returns an int in the range 0 to 255
 * for each byte read, and returns -1 only when the end of the stream is reached.
 * If that value is cast to a  byte before being compared with -1,
 * a data byte of (255) becomes -1 and is mistaken for the end of
 * the file, causing the loop to stop early.
 * fails_with_0xFF.bin is has a 255 value in it to show the issue.
 */
public class Main4
{
    public static void main(String [] args)
    {
        try(FileInputStream in = new FileInputStream("fails_with_0xFF.bin")) //opens the file to be read from (a binary file)
        {
            int data; //the data were reading from the file into

            while((data = (byte) in.read()) != -1) //keep reading until end of file. By converting to byte prematurely we risk ending the data too early
            {
                System.out.print( data);
            }
            System.out.println();
        }
        catch(IOException e)
        {
            System.out.println("Could not read file.");
        }
    }
}