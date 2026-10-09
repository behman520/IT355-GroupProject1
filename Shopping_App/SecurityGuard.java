// A snapshot of a user's shopping cart that can be written to / read from a file.
// It keeps its own small copy of the cart data, so Item and ShoppingCart are not changed.
 
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public final class SavedCart implements Serializable {
    private static final long serialVersionUID = 1L;
 
    // one saved cart line (name, price, amount)
    static final class Entry implements Serializable {
        private static final long serialVersionUID = 1L;
 
        private String name;
        private double price;
        private int amount;
 
        Entry(String name, double price, int amount) {
            validate(name, price, amount); // MET00-J: validate method arguments
            this.name = name;
            this.price = price;
            this.amount = amount;
        }
 
        String getName() { return name; }
        double getPrice() { return price; }
        int getAmount() { return amount; }
 
        // MET00-J: the rules an entry must always follow
        private static void validate(String name, double price, int amount) {
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("item name must not be empty");
            if (Double.isNaN(price) || Double.isInfinite(price) || price < 0)
                throw new IllegalArgumentException("item price must be zero or more");
            if (amount < 0)
                throw new IllegalArgumentException("item amount must not be negative");
        }
 
        // SER01-J: exact signature (private, non-static) so the JVM calls it
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }
 
        // SER01-J: exact signature so the JVM calls it; the constructor is skipped
        // during deserialization, so the same rules are re-checked here
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            try {
                validate(name, price, amount);
            } catch (IllegalArgumentException e) {
                throw new InvalidObjectException(e.getMessage());
            }
        }
    }
 
    private ArrayList<Entry> entries;
 
    SavedCart(List<Item> cartItems) {
        SecurityGuard.require("cart.access"); // SER04-J: the check in the constructor...
 
        if (cartItems == null) // MET00-J: validate method arguments
            throw new IllegalArgumentException("items must not be null");
 
        this.entries = new ArrayList<>();
        for (Item item : cartItems)
            entries.add(new Entry(item.getName(), item.getPrice(), item.getAmount()));
    }
 
    List<Entry> getEntries() { // package-private: Entry is not a public type
        return List.copyOf(entries); // read-only view
    }
 
    // SER01-J: exact signature so the JVM calls it
    // SER04-J: ...is repeated here, because serialization can expose the cart's state
    private void writeObject(ObjectOutputStream out) throws IOException {
        SecurityGuard.require("cart.access");
        out.defaultWriteObject();
    }
 
    // SER01-J: exact signature so the JVM calls it
    // SER04-J: deserialization skips the constructor, so the same check is repeated here
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        SecurityGuard.require("cart.access");
        in.defaultReadObject();
 
        if (entries == null || entries.contains(null)) // validate what came out of the stream
            throw new InvalidObjectException("invalid saved cart");
    }
}
