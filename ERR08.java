public class ERR08 {
    public static void printLength(String str) {
        if (str == null) { // Compliant: Explicit validation instead of catching NPE
            System.out.println("String is null");
        } else {
            System.out.println(str.length());
        }
    }
}
