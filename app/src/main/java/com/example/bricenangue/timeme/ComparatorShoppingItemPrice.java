package com.example.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by praktikum on 01/07/15.
 * Helper class to sort values.
 */
public class ComparatorShoppingItemPrice implements Comparator<ShoppingItem> {
    @Override
    public int compare(ShoppingItem lhs, ShoppingItem rhs) {
        //Remove the dots from the value
        String value1 = lhs.getTotalPriceofItemstoBuy().replace(".","");
        String value2 = rhs.getTotalPriceofItemstoBuy().replace(".","");

        //Replace comma with dots
        Float value1Float = Float.parseFloat(value1.replace(",","."));
        Float value2Float = Float.parseFloat(value2.replace(",","."));

        int idk = value1Float.compareTo(value2Float);

        return idk;
    }
    @Override
      public boolean equals(Object object) {
        return false;
    }


}
