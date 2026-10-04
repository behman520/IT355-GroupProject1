//this represents the users current shopping cart of items

import java.util.ArrayList;

public class ShoppingCart {

    private ArrayList<Item> cartList; // list of Items

    public void removeItem(Item i) // remove the specified item from the shopping cart
    {
        for (Item inList : cartList) // loop for each item in the list
        {
            if (inList.getItemID().equals(i.getItemID())) // find the one that matches the id of the parameter item
            {
                if (inList.getAmount() > 1) // if there is more than one item, just decrement the amount
                {
                    inList.decrementAmount();
                } else // remove the item if its at one for amount
                {
                    cartList.remove(inList);
                }
                break;
            }
        }
    }
}
