import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.NoSuchFileException;

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