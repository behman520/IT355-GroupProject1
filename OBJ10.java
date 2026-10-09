/**
 * OBJ10-J: Do not use public static nonfinal fields.
 *
 * <p>Any code, in any thread, can change a public static nonfinal field. Declaring it
 * {@code final} means the value can never be reassigned.
 */
public class OBJ10 {

    /** A public constant: {@code final}, so it can never be reassigned. */
    public static final int MAX_USERS = 100;

    /**
     * Prints the constant.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("MAX_USERS = " + MAX_USERS);
    }
}