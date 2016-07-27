package com.example.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by praktikumfh on 09/06/16.
 * Helper class to sort values. Here the item that has been selected
 */
public class ComparatorItemSelected implements Comparator<ShoppingItem>

    {

        @Override
        public int compare(ShoppingItem lhs, ShoppingItem rhs) {
        boolean boolL = lhs.isItemIsBought();
        boolean boolR = rhs.isItemIsBought();

        if( boolL && ! boolR ) {
            return +1;
        }
        if( ! boolL && boolR ) {
            return -1;
        }
        return 0;
    }

        @Override
        public boolean equals(Object object) {
        return false;
    }
}
