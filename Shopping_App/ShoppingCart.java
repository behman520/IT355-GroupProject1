//this represents the users current shopping cart of items

import java.util.ArrayList;

public class ShoppingCart {

    private ArrayList<Item> cartList; // list of Items

    ShoppingCart()
    {
        this.cartList = new ArrayList<>();
    }

    //OLDER METHODS

    public boolean containsItem(String name)
    {
        for(Item e : cartList)
        {
            if(e.getName().equals(name))
            {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Item> getList()
    {
        return this.cartList;
    }

}
