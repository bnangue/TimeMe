package com.app.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by praktikum on 01/07/15.
 * Helper class to sort values. Here a name
 */
public class ComparatorShoppingItemName implements Comparator<ShoppingItem> {

    @Override
    public int compare(ShoppingItem lhs, ShoppingItem rhs) {
        String string1 = lhs.getItemName();
        String string2 = rhs.getItemName();

        return string1.compareTo(string2);
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }

}

