import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.NoSuchFileException;


/**
 * Demonstrates a time-of-check to time-of-use (TOCTOU) problem when reading a
 * file in Java.
 * The program checks that file.txt exists and then opens a file for
 * reading. Because the File variable is reassigned between the check and
 * the open, the file actually read is mwahaha.txt, not the one that was
 * verified. This shows how, in a real system, a file could be swapped or
 * replaced by another party after it has been checked but before it is used.
 * The safer better is to skip the separate existence check and simply attempt
 * to open the file inside a try-with-resources block, handling any
 * IOException if it fails.
 */
public class Main
{
    public static void main(String[] args) {
        File file = new File("file.txt");
        File hackerFile = new File("mwahaha.txt");

        //try(BufferedReader reader = new BufferedReader(new FileReader("file.txt")))
        //{
           // String line = "";
          //  while((line = reader.readLine()) != null)
         //       System.out.println(line);
        //}
        //catch(IOException e)
        //{

        //}

        if(file.exists())
        {
            file = hackerFile;
            try(BufferedReader reader = new BufferedReader(new FileReader(file)))
            {
                String line = "";
                while((line = reader.readLine()) != null)
                    System.out.println(line);
            }
            catch(IOException e)
            {

            }
        }


    }
}