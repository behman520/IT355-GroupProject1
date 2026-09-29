/*this program uses the return value from the FILE class appropriately
handing any errors opening files correctly
It is implemented as a program that allows you to play songs, however,
if a song is missing the program handles the exception*/

import java.io.File;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;


public class Main
{
    static String SHAPE_OF_YOU = "C:\\Users\\ajrose6\\Aaron_Code\\FIO02-J\\Ed Sheeran Shape of You (Lyrics).wav";
    static String UPTOWN_FUNK = "C:\\Users\\ajrose6\\Aaron_Code\\FIO02-J\\Mark Ronson - Uptown Funk (Lyrics) ft. Bruno Mars.wav";
    static String CANTFEELMYFACE = "C:\\Users\\ajrose6\\Aaron_Code\\FIO02-J\\The Weeknd - Cant Feel My Face (Official Audio).wav";
    static String FIREWORK = "C:\\Users\\ajrose6\\Aaron_Code\\FIO02-J\\Katy Perry - Firework (Lyrics).mp3"; // .mp3 file, won't work
    static String ROLLINGINTHEDEEP;

    Clip clip; //clip used to play music


    public static void menu()
    {
        System.out.println("1 -- SHAPE OF YOU");
        System.out.println("2 -- UPTOWN FUNK");
        System.out.println("3 -- CANT FEEL MY FACE");
        System.out.println("4 -- FIREWORK");
        System.out.println("5 -- ROLLING IN THE DEEP");
    }

    public static void main(String [] args)
    {

        Main m = new Main();

        boolean continueListening = true; //when false the program ends

        int choice = 0; //option the user chooses

        Scanner scan = new Scanner(System.in); //get input from user

        System.out.println("Hello, welcome to the song o' matic 2000. Choose a song from the list to play! ");

        menu(); //show the user the menu

        System.out.print("Enter choice:");
        choice = scan.nextInt();
        scan.nextLine();

        while(continueListening) //keep playing while user doesn't choose to quit
        {
            switch(choice)
            {
                case 1:
                    m.playMusic(SHAPE_OF_YOU);
                break;

                case 2:
                    m.playMusic(UPTOWN_FUNK);
                break;

                case 3:
                    m.playMusic(CANTFEELMYFACE);
                break;

                case 4:
                    m.playMusic(FIREWORK);
                break;

                case 5:
                    m.playMusic(ROLLINGINTHEDEEP);
                break;

                case 6: //user quits
                    continueListening = false;
                break;
            }
            menu(); 
            System.out.print("Enter choice:");
            choice = scan.nextInt();
            scan.nextLine();
            m.stopPlayingMusic(); //stop playing the current song
        }

        scan.close();
    }

    public void playMusic(String filePath) //plays music 
    {
        try
        {
            File musicPath = new File(filePath); //we open the file in a try catch block so the code only continues if the path exists
            if(musicPath.exists()) //checking the return value of the music path instead of just assuming it exists
            {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
            else
            {
                System.out.println("Cannot find file in the current directory, please try a different file");
            }
        
        }
        catch(NullPointerException e)
        {
            System.out.println("File doesn't exist");
        }
        catch(UnsupportedAudioFileException e)
        {
            System.out.println("\nOnly .wav files are supported!\n");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
    }

    public void stopPlayingMusic()
    {
        try
        {
            if(clip.isRunning())
            clip.stop();
        }
        catch(Exception e)
        {
            
        }
         

    }
}