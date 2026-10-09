import java.util.Arrays;


/**
 * Demonstrates the difference between reference equality and content equality
 * when comparing arrays in Java.
 * <p>
 * Arrays inherit the Object.equals method, which only checks whether two
 * variables refer to the same array object. To compare the actual
 * contents of two arrays, use Arrays.equals() method
 * This class also shows how aliasing works: two variables can point to the same
 * array, so changes made through one are visible through the other.
 */
public class Main1
{

    public static void main(String[] arg)
    {
        int[] array1 = new int[10];
        int[] array2 = new int[10];
        int[] array3 = array2;

        System.out.println("array1.equals(array2): " + array1.equals(array2));
        System.out.println("array3.equals(array2): " + array3.equals(array2));
        System.out.println("Arrays.equals(array1, array2): " + Arrays.equals(array1, array2));

        System.out.println("\n");

        //try to change the elements in array 3 (actually changing elements in array2)

        for(int i = 0; i < 10; i++)
        {
            array3[i] = i;
        }

        //now comparing array one and two using Arrays.equals will be false, but array3 will still equal array2
        System.out.println("array3.equals(array2): " + array3.equals(array2));
        System.out.println("Arrays.equals(array1, array2): " + Arrays.equals(array1, array2));
    }

}