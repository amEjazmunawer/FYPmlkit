package com.google.firebase.samples.apps.mlkit.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.samples.apps.mlkit.java.Product;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.google.firebase.samples.apps.mlkit.common.DatabaseLite.DbaseEntry.TABLE_NAME;

public final class DatabaseLite {

    private DatabaseLite() {}
    private static final String TAG ="DatabaseLite";
    public static class DbaseEntry implements BaseColumns {



        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_INFO = "information";
        public static final String COLUMN_NAME_LINK = "weblink";

    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbaseEntry.TABLE_NAME + " (" +
                    DbaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DbaseEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    DbaseEntry.COLUMN_NAME_LABEL + " TEXT," +
                    DbaseEntry.COLUMN_NAME_DESC + " TEXT,"  +
                    DbaseEntry.COLUMN_NAME_INFO + " TEXT,"   +
                    DbaseEntry.COLUMN_NAME_LINK + " TEXT)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbaseEntry.TABLE_NAME;


    public static class FeedReaderDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "ShopBase.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }


    public static class DBaseQc {

        public static Context cont;

        public static List<Product>  QueryLabel(String[] label) {

            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(cont);

            SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    BaseColumns._ID,
                    DbaseEntry.COLUMN_NAME_IMAGE,
                    DbaseEntry.COLUMN_NAME_DESC,
                    DbaseEntry.COLUMN_NAME_LABEL,
                    DbaseEntry.COLUMN_NAME_INFO,
                    DbaseEntry.COLUMN_NAME_LINK
            };

// Filter results WHERE "title" = 'My Title'
            String selection = "";
            for(int i=0; i<label.length-1;++i)
            {
                selection += DbaseEntry.COLUMN_NAME_LABEL + " = ? OR " ;
            }
            selection += DbaseEntry.COLUMN_NAME_LABEL + " = ?";
//            String[] selectionArgs = new String[label.size()];
//            label.toArray(selectionArgs);

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    DbaseEntry.COLUMN_NAME_DESC ;

            Cursor cursor = dbRead.query(
                    DbaseEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    label,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            List<Product> products = new ArrayList<>();
//            List itemIds = new ArrayList<>();
            while(cursor.moveToNext()) {
//                long itemId = cursor.getLong(
//                        cursor.getColumnIndexOrThrow(DbaseEntry._ID));
//                cursor.
//                itemIds.add(itemId);
                products.add(new Product(cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_IMAGE)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_LABEL)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_DESC)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_INFO)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_LINK))));
            }
            cursor.close();

            for (Product p :
                    products) {
                Log.d(TAG,p.toString());
            }
            return products;
        }

        public static void initialise(Context context) {

            if(context.getDatabasePath(FeedReaderDbHelper.DATABASE_NAME).exists())
            {
                Log.d(TAG, "Deleting DBase");
                context.deleteDatabase(FeedReaderDbHelper.DATABASE_NAME);
            }
            cont = context;
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
            // Gets the data repository in write mode
            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
            Cursor dbCursor = dbRead.query(TABLE_NAME, null, null, null, null, null, null);

            String[] columnNames = dbCursor.getColumnNames();
            dbWrite.isOpen();


            for (String column : columnNames)
            {
                Log.d(TAG, "Column name:" +column);
            }

// Create a new map of values, where column names are the keys
//              ContentValues values = new ContentValues();
//             values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_IMAGE, "insertedimage");
//              values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_LABEL, "labeltesting");
//               values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_DESC, "testdescription");

//               values.put(DatabaseLite.DbaseEntry.COLUMN_NAME_LINK, "lonkponk");

            List<Product> products = new ArrayList<>();

            products.add(new Product("https://my-test-11.slatic.net/p/6681aff3a5a7f8762965de8ccdaec11e.jpg","Mobile phone","Realme5","The Realme 5 is the bigger phone of the duo due to its larger 6.5\" display and beefier 5,000 mAh battery.","https://www.gsmarena.com/realme_5-9802.php"));
            products.add(new Product("https://i.ebayimg.com/00/s/NjQwWDY0MA==/z/1gQAAOSw7KddA7Fu/$_57.JPG?set_id=8800005007","Cereal","Koko Krunch","Koko Krunch is a chocolate-flavored whole-grain breakfast cereal distributed by Nestlé in most of Europe, Asia, the Middle-East and Latin America.","https://www.nestle-cereals.com/ph/en/products-promotions/brands/koko-krunch-brand"));
            products.add(new Product("https://my-live-01.slatic.net/original/22011b4a201f40d918bb57ca656a643b.jpg","Mobile phone","iPhone XR","What you still get is the same general iPhone X series design, the same powerful A12 Bionic chipset from the XS, the same TrueDepth camera  and the single camera on the back is the main 12MP sensor. ","https://www.gsmarena.com/apple_iphone_xr-9320.php"));
            products.add(new Product("https://images-na.ssl-images-amazon.com/images/I/81QpkIctqPL._AC_SL1500_.jpg","Monitor","Acer SB220Q","Acer SB220Q bi 21.5\" Full HD (1920 x 1080) IPS Ultra-Thin Zero Frame Monitor (HDMI & VGA Port) ","https://www.newegg.com/acer-sb220q-bi-21-5/p/0JC-000P-00AT1"));
            products.add(new Product("http://www.marigold.com.my/assets/images/contents/products/milk/hl-milk/showcase/low-fat-milk/strawberry/full.jpg","Milk","HL Strawb","MARIGOLD HL Low Fat Milk is more than just ordinary milk. It is high in calcium and protein while low in fat and lactose, also fortified with the 9 essential vitamins.","http://www.marigold.com.my/products/milk/hl-milk.html"));
            products.add(new Product("https://my-test-11.slatic.net/p/1249e55443f3d3901359601f2035b6a9.jpg","Clock","Apple Watch 5","Glass front (Sapphire crystal), ceramic/sapphire crystal back, stainless steel frame","https://www.gsmarena.com/apple_watch_series_5-9859.php"));
            products.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61SVEUsMepL._AC_SX522_.jpg","Mobile phone","Samsung Galaxy A20","To go with the bigger display, battery capacity has been bumped to 4,000mAh, and the phone has gotten heavier in the process, but at 169g it's still among the lighter offerings.","https://www.gsmarena.com/samsung_galaxy_a20-9640.php"));
            products.add(new Product("https://fdn2.gsmarena.com/vv/bigpic/motorola-razr-2019-.jpg","Mobile phone","Motorola RAZR Fold","Folding screen smartphone, released in the year 2020 with good Moto branding","https://www.gsmarena.com/motorola_razr_2019-9630.php"));

            //Product p = new Product("imagelink","imagelink","imagelink","imagelink");
// Insert the new row, returning the primary key value of the new row

            for (Product p:products) {

                long newRowId = dbWrite.insert(TABLE_NAME, null, p.getContentValues());
                Log.d(TAG, "New row ID is :" + newRowId);

            }





        }



    }




}
