//this represents the users current shopping cart of items

import java.util.ArrayList;

public class ShoppingCart {

    private ArrayList<Item> cartList; // list of Items

    ShoppingCart()
    {
        this.cartList = new ArrayList<>();
    }













    //OLDER METHODS

    public boolean removeItem(String itemName) // remove the specified item from the shopping cart
    {
        boolean retValue = false;

        for (Item inList : cartList) // loop for each item in the list
        {
            if (inList.getName().equals(itemName)) // find the one that matches the id of the parameter item
            {
                if (inList.getAmount() > 1) // if there is more than one item, just decrement the amount
                {
                    inList.decrementAmount();
                    retValue = true;
                } else // remove the item if its at one for amount
                {
                    cartList.remove(inList);
                    retValue = true;
                }
                break;
            }
        }

        return retValue;
    }

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
