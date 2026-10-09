import java.util.Arrays;

/**
 * OBJ13-J: Ensure that references to mutable objects are not exposed.
 *
 * <p>A {@code final} array reference does not stop clients changing the array's
 * elements. The array is kept private and an accessor returns a copy.
 */
public class OBJ13 {

    /** Private array; never handed out directly. */
    private static final String[] COLORS = { "red", "green", "blue" };

    /**
     * Returns the colors.
     *
     * @return a copy of the array; changing it does not affect the original
     */
    public static String[] colors() {
        return COLORS.clone();
    }

    /**
     * Shows that changing the returned array does not change the original.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        colors()[0] = "changed"; // only changes the copy
        System.out.println(Arrays.toString(colors())); // [red, green, blue]
    }
}