package com.example.bricenangue.timeme;

import java.util.Comparator;

/**
 * Created by bricenangue on 10/08/16.
 */
public class ComparatorSortFinanceDetailsBookingDate implements Comparator<FinanceRecords> {

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
