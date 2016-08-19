package com.app.bricenangue.timeme;

/**
 * Created by praktikum on 27/01/16.
 */
public class EntryItem implements Item{

    public final String title;


    public EntryItem(String title) {
        this.title = title;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
