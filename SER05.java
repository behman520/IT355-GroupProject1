import java.io.Serializable;

public class SER05 {
    private static String outerField = "Outer data";

    // Compliant: Static nested class breaks the implicit enclosing reference
    public static class NestedClass implements Serializable {
        public void display() {
            System.out.println(outerField); 
        }
    }
}
