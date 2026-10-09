// Stand-in for the Java SecurityManager, which is deprecated and cannot be enabled in Java 24+.
// Used by SER04-J (SavedCart) and SER08-J (CartStorage.load).
 
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
 
public final class SecurityGuard {
 
    // private, so this is not a public static non-final field (OBJ10-J)
    private static Set<String> granted = Set.of();
 
    private SecurityGuard() {
    }
 
    // a signed-in user holds these permissions (more than reading a saved cart needs)
    public static void signIn() {
        granted = Set.of("cart.access", "db.write");
    }
 
    public static void signOut() {
        granted = Set.of();
    }
 
    // throws if the permission is not currently granted (message kept generic: ERR01-J)
    public static void require(String permission) {
        if (!granted.contains(permission))
            throw new SecurityException("access denied");
    }
 
    // SER08-J: runs the action with ONLY the listed permissions (it can only narrow what is
    // already granted, never add), like AccessController.doPrivileged(action, restrictedContext).
    // The action is a Supplier, so no broad "throws Exception" is needed (ERR08-J).
    public static <T> T withPermissions(Set<String> needed, Supplier<T> action) {
        Set<String> previous = granted;
        Set<String> restricted = new HashSet<>(needed);
        restricted.retainAll(previous);
        granted = restricted;
        try {
            return action.get();
        } finally {
            granted = previous; // always restore
        }
    }
}
