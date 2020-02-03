package com.google.firebase.samples.apps.mlkit.java;

import android.content.ContentValues;

import com.google.firebase.samples.apps.mlkit.common.DatabaseLite;

public class Product {

    public String Image;
    public String Label;
    public String Desc;
    public String Link;

    public Product(String _Image, String _Label, String _Desc, String _Link) {

        Image = _Image;
        Label = _Label;
        Desc = _Desc;
        Link = _Link;

    }

    public Product() {


    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_IMAGE, Image);
        values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_LABEL, Label);
        values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_DESC, Desc);
        values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_LINK, Link);

        return values;

    }

    @Override
    public String toString() {
        return  DatabaseLite.DbaseEntry.COLUMN_NAME_IMAGE + ":" + Image + ",  " +
                DatabaseLite.DbaseEntry.COLUMN_NAME_LABEL + ":" + Label + ",   " +
                DatabaseLite.DbaseEntry.COLUMN_NAME_DESC + ":" + Desc + ",   " +
                DatabaseLite.DbaseEntry.COLUMN_NAME_LINK + ":" + Link;

    }

}
