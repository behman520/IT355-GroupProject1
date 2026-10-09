//this represents the users current shopping cart of items

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ShoppingCart {

    private final List<Item> cartList; // list of Items

    ShoppingCart() {
        this.cartList = new ArrayList<>();
    }

   public boolean isEmpty() {
        return cartList.isEmpty();
    }

    public int getAmountOf(String name) {
        Item found = find(name);
        return found == null ? 0 : found.getAmount();
    }

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

    public double getSubtotal() {
        double total = 0.0;
        for (Item item : cartList) {
            total += item.getPrice() * item.getAmount();
        }
        return total;
    }

    private Item find(String name) {
        for (Item item : cartList) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getList() {
        List<Item> copy = new ArrayList<>();
        for (Item item : cartList) {
            copy.add(new Item(item));
        }
        return Collections.unmodifiableList(copy);
    }

}
