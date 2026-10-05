package MET52J;

public class Main {
    
    public static void main()
    {

        Person p1 = new Person("Harry Osborne", 19);
        Person p2 = new Person("Peter Parker", 19);

        EvilClone clone1 = new EvilClone(p1);

        NormalClone clone2 = new NormalClone(p2);

        
        EvilClone clone3 = clone1.clone();
        NormalClone clone4 = clone2.clone();

        clone3.getPerson().setName("Norman Osborne");
        clone4.getPerson().setName("May Parker");

        System.out.println(clone1.getPerson().getName());
        System.out.println(clone3.getPerson().getName());
        System.out.println(clone2.getPerson().getName());
        System.out.println(clone4.getPerson().getName());
    }
}
