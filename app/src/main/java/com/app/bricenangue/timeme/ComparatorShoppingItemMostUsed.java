package com.app.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by praktikum on 01/07/15.
 */
public class ComparatorShoppingItemMostUsed implements Comparator<ShoppingItem> {
    @Override
    public int compare(ShoppingItem lhs, ShoppingItem rhs) {
        Integer value1 = lhs.getNumberoftimeAddedAnyToList();
        Integer value2 = rhs.getNumberoftimeAddedAnyToList();


        int idk = value1.compareTo(value2);

        return idk;
    }
    @Override
    public boolean equals(Object object) {
        return false;
    }
}
