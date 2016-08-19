package com.app.bricenangue.timeme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by bricenangue on 10/08/16.
 */
public class ComparatorSortFinanceDetailsBookingDate implements Comparator<FinanceRecords> {

    @Override
    public int compare(FinanceRecords lhs, FinanceRecords rhs) {
        Long string1 = milliseconds(lhs.getRecordBookingDate());
        Long string2 = milliseconds(rhs.getRecordBookingDate());

        return string1.compareTo(string2);
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }



    public long milliseconds(String date)
    {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }
}
