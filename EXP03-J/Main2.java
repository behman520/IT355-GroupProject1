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