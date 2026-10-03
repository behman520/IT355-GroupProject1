import java.io.FileInputStream;
import java.io.IOException;

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