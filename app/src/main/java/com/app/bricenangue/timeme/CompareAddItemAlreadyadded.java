package com.app.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by bricenangue on 27/07/16.
 */
public class CompareAddItemAlreadyadded implements Comparator<ShoppingItem> {
    @Override
    public int compare(ShoppingItem lhs, ShoppingItem rhs) {
        //Remove the dots from the value
        Integer value1 = lhs.getNumberofItemsetForList();
        Integer value2 = rhs.getNumberofItemsetForList();


        int idk = value1.compareTo(value2);

        return idk;
    }
    @Override
    public boolean equals(Object object) {
        return false;
    }
}
