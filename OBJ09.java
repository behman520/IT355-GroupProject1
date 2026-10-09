/**
 * OBJ09-J: Compare classes and not class names.
 *
 * <p>Two classes are the same only if they have the same name and the same class
 * loader. Comparing {@code Class} objects checks both; comparing names checks neither.
 */
public class OBJ09 {

    /** The one handler class that is trusted. */
    static class Handler { }

    /**
     * Checks whether an object is the trusted handler.
     *
     * @param candidate the object to check
     * @return {@code true} only if the object's class is exactly {@link Handler}
     */
    static boolean isTrustedHandler(Object candidate) {
        return candidate.getClass() == Handler.class;
    }

    /**
     * Runs the check on a real handler and on an object of another class.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Handler trusted? " + isTrustedHandler(new Handler())); // true
        System.out.println("String trusted?  " + isTrustedHandler("text"));// false
    }
}