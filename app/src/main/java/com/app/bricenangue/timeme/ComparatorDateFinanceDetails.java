package com.app.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by bricenangue on 19/08/16.
 */
public class ComparatorDateFinanceDetails implements Comparator<FinanceRecords> {

    @Override
    public int compare(FinanceRecords lhs, FinanceRecords rhs) {
        String string1 = lhs.getRecordBookingDate();
        String string2 = rhs.getRecordBookingDate();

        return string1.compareTo(string2);
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
