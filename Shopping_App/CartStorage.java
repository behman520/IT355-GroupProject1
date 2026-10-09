// Saves a user's shopping cart to a file when they exit and restores it when they sign in.
// Runner calls load() right after login and save() when the user exits.
 
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
 
// OBJ11-J: the cart file is a serialized object, not a text file, so it cannot be edited by the user
public final class CartStorage {
    // OBJ10-J: all static fileds are private static final
    private static final Logger logger = Logger.getLogger(CartStorage.class.getName());
 
    // SER12-J: the ONLY classes allowed to be deserialized from a cart file
    private static final Set<String> ALLOWED_CLASSES = Set.of(
            SavedCart.class.getName(),
            SavedCart.Entry.class.getName(),
            ArrayList.class.getName());
 
    private CartStorage() {
    }
 
    // SER12-J: checks every class name in the stream BEFORE the class is loaded or created
    private static final class WhitelistObjectInputStream extends ObjectInputStream {
        WhitelistObjectInputStream(InputStream in) throws IOException {
            super(in);
        }
 
        @Override
        protected Class<?> resolveClass(ObjectStreamClass cls) throws IOException, ClassNotFoundException {
            if (!ALLOWED_CLASSES.contains(cls.getName()))
                throw new InvalidClassException(cls.getName(), "not on whitelist");
            return super.resolveClass(cls);
        }
    }
 
    // the file name uses the numeric user id, never text the user typed
    private static Path cartFile(User user) {
        return Path.of("Database", "cart_" + user.getID() + ".ser");
    }
 
    // writes the user's cart to disk (called when the user exits)
    public static void save(User user) {
        if (user == null) // MET00-J: validate method arguments
            throw new IllegalArgumentException("user must not be null");
 
        Path file = cartFile(user);
        try {
            Files.createDirectories(file.getParent());
            SavedCart snapshot = new SavedCart(user.getCartItems());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(snapshot);
            }
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            logger.log(Level.WARNING, "Could not save the shopping cart.", e);
            System.out.println("\nCouldn't save your shopping cart.");
        } finally {
            SecurityGuard.signOut(); // the session is over
        }
    }
 
    // reads the user's saved cart back in (called right after sign in)
    public static void load(User user) {
        if (user == null) // MET00-J: validate method arguments
            throw new IllegalArgumentException("user must not be null");
 
        SecurityGuard.signIn(); // the signed-in user now holds permissions
        Path file = cartFile(user);
        try {
            // SER08-J: deserialize with ONLY the permission this task needs,
            // even though a signed-in user holds more
            SavedCart saved = SecurityGuard.withPermissions(Set.of("cart.access"), () -> {
                try {
                    return read(file);
                } catch (IOException e) {
                    throw new UncheckedIOException(e); // a Supplier cannot throw checked exceptions
                }
            });
            restoreInto(user, saved);
 
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof NoSuchFileException)
                return; // no saved cart yet, nothing to restore
            logger.log(Level.WARNING, "Could not restore the saved shopping cart.", e);
            System.out.println("\nCouldn't restore your saved cart.");
        } catch (SecurityException | IllegalArgumentException | UnsupportedOperationException e) {
            logger.log(Level.WARNING, "Could not restore the saved shopping cart.", e);
            System.out.println("\nCouldn't restore your saved cart.");
        }
    }
 
    // puts the saved items into the user's cart, using fresh price and stock from the database
    private static void restoreInto(User user, SavedCart saved) {
        int before = totalUnits(user);
        int added = 0;
        Map<String, Integer> restored = new HashMap<>(); // units restored so far, per item name
 
        for (SavedCart.Entry entry : saved.getEntries()) {
            String name = entry.getName();
            if (!Controller.checkIfItemIsInList(name))
                continue; // item no longer exists
 
            // the price and stock come from the database, never from the file
            double price = Controller.returnItemPrice(name);
            int room = Controller.getStockAmount(name) - restored.getOrDefault(name, 0);
            int amount = Math.min(entry.getAmount(), room);
            if (price < 0 || amount <= 0)
                continue; // lookup failed or no stock left
 
            user.restoreCartItem(name, price, amount);
            restored.merge(name, amount, Integer::sum);
            added += amount;
        }
 
        // make sure the cart really holds the restored items
        if (totalUnits(user) != before + added) {
            logger.log(Level.WARNING, "The shopping cart did not keep the restored items.");
            System.out.println("\nCouldn't restore your saved cart.");
            return;
        }
 
        if (added > 0)
            System.out.println("\nRestored " + added + " item(s) from your saved cart.");
    }
 
    // how many items (counting each unit) are in the user's cart right now
    private static int totalUnits(User user) {
        int total = 0;
        for (Item item : user.getCartItems())
            total += item.getAmount();
        return total;
    }
 
    // ClassNotFoundException is reported as an IOException, so callers deal with one type
    private static SavedCart read(Path file) throws IOException {
        try (ObjectInputStream in = new WhitelistObjectInputStream(Files.newInputStream(file))) {
            Object obj = in.readObject();
            if (!(obj instanceof SavedCart)) //OBJ09-J: compares Classes, not class names
                throw new InvalidObjectException("unexpected data");
            return (SavedCart) obj;
        } catch (ClassNotFoundException e) {
            throw new InvalidClassException("unknown class in saved cart");
        }
    }
}
