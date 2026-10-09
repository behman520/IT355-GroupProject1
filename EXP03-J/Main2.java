/**
 * Demonstrates the difference between reference equality (==) and value
 * equality (Integer.equals(Object)) when comparing Integer
 * objects in Java.
 * <p>
 * When an int is autoboxed into an  Integer wrapper class, Java uses
 * Integer.valueOf(int), which returns a cached object for values in the
 * range -128 to 127. Values outside that range produce a new object each time,
 * so == compares different references even when the numeric values match.
 */
public class Main2
{
    public static void main(String[] args)
    {
       Integer x = 127;
       Integer y = 127;

       Integer xx = 128;
       Integer yy = 128;

        
       //notice that x and y are equivalent by not xx and yy because
       //128 is out of the cached range of the Integer object
       System.out.println(x == y);
       System.out.println(xx == yy);
       //however .equals() method works for Integer, because it compares
       //values, not references
       System.out.println(xx.equals(yy));

       
    }
}