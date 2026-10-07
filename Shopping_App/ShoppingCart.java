//this represents the users current shopping cart of items

import java.util.ArrayList;

public class ShoppingCart {

    private ArrayList<Item> cartList; // list of Items

    ShoppingCart() {
        this.cartList = new ArrayList<>();
    }

    //finds and returns an item in the shopping cart
    public Item findItem(String name)
    {
        for(Item e : cartList)
        {
            if(e.getName().equals(name))
            {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Item> getList() {
        return this.cartList;
    }

}
