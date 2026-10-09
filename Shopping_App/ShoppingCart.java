//this represents the users current shopping cart of items

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user's current shopping cart.
 * <p>
 * The cart holds at most one {@link Item} entry per item name. Adding an item
 * that is already in the cart increases its quantity rather than creating a
 * duplicate entry, and removing the last unit of an item removes the entry
 * from the cart entirely.
 * </p>
 */
public class ShoppingCart {

    /** The items currently in the cart, one entry per distinct item name. */
    private final List<Item> cartList; // list of Items

    /**
     * Constructs a new, empty shopping cart.
     * <p>
     * This constructor is package-private, so it can only be called from
     * classes within the same package.
     * </p>
     */
    ShoppingCart() {
        this.cartList = new ArrayList<>();
    }

    /**
     * Checks whether the cart contains any items.
     *
     * @return {@code true} if the cart is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return cartList.isEmpty();
    }

    /**
     * Returns the quantity of a given item in the cart.
     *
     * @param name the exact name of the item (case-sensitive)
     * @return the number of units of that item in the cart, or {@code 0} if
     *         the item is not in the cart
     */
    public int getAmountOf(String name) {
        Item found = find(name);
        return found == null ? 0 : found.getAmount();
    }

    /**
     * Adds one unit of an item to the cart.
     * <p>
     * If the item is not yet in the cart, a new entry is created with a
     * quantity of 1 and the given price. If it is already in the cart, its
     * quantity is increased by one and the supplied price is ignored (the
     * originally stored price is kept).
     * </p>
     *
     * @param name  the exact name of the item (case-sensitive)
     * @param price the price of a single unit, used only when the item is not
     *              already in the cart
     */
    public void addOne(String name, double price) {
        Item found = find(name);
        if (found == null) {
            Item newItem = new Item(name, price);
            newItem.setAmount(1);
            cartList.add(newItem);
        } else {
            found.incrementAmount();
        }
    }

    /**
     * Removes one unit of an item from the cart.
     * <p>
     * If more than one unit is in the cart, the quantity is decreased by one.
     * If only one unit remains, the item's entry is removed from the cart
     * entirely.
     * </p>
     *
     * @param name the exact name of the item (case-sensitive)
     * @return {@code true} if a unit was removed; {@code false} if the item
     *         was not in the cart
     */
    public boolean removeOne(String name) {
        Item found = find(name);
        if (found == null) {
            return false;
        }
        if (found.getAmount() > 1) {
            found.decrementAmount();
        } else {
            cartList.remove(found);
        }
        return true;
    }

    /**
     * Calculates the subtotal of the cart.
     * <p>
     * The subtotal is the sum of each item's unit price multiplied by its
     * quantity. It does not include tax, shipping or discounts.
     * </p>
     *
     * @return the subtotal of all items in the cart, or {@code 0.0} if the
     *         cart is empty
     */
    public double getSubtotal() {
        double total = 0.0;
        for (Item item : cartList) {
            total += item.getPrice() * item.getAmount();
        }
        return total;
    }

    /**
     * Finds the cart entry for an item by name.
     *
     * @param name the exact name of the item (case-sensitive)
     * @return the matching {@link Item} stored in the cart, or {@code null}
     *         if no item with that name is in the cart
     */
    private Item find(String name) {
        for (Item item : cartList) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns a read-only snapshot of the items in the cart.
     * <p>
     * Each item in the returned list is a copy, so neither changing the
     * returned items nor changing the cart afterward affects the other. The
     * list itself is unmodifiable; attempting to add or remove elements
     * throws {@link UnsupportedOperationException}. Use {@link #addOne} and
     * {@link #removeOne} to change the cart's contents.
     * </p>
     *
     * @return an unmodifiable list of copies of the cart's items
     */
    public List<Item> getList() {
        List<Item> copy = new ArrayList<>();
        for (Item item : cartList) {
            copy.add(new Item(item));
        }
        return Collections.unmodifiableList(copy);
    }

}
