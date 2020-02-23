package com.google.firebase.samples.apps.mlkit.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import com.google.firebase.samples.apps.mlkit.java.Product;
import java.util.ArrayList;
import java.util.List;
import static com.google.firebase.samples.apps.mlkit.common.DatabaseLite.DbaseEntry.TABLE_NAME;

public final class DatabaseLite {

    private DatabaseLite() {}
    private static final String TAG ="DatabaseLite";
    public static class DbaseEntry implements BaseColumns {

        public static final String TABLE_NAME = "Products";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_LABEL2 = "label2";
        public static final String COLUMN_NAME_DESC = "description";
        public static final String COLUMN_NAME_INFO = "information";
        public static final String COLUMN_NAME_LINK = "weblink";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbaseEntry.TABLE_NAME + " (" +
                    DbaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DbaseEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    DbaseEntry.COLUMN_NAME_LABEL + " TEXT," +
                    DbaseEntry.COLUMN_NAME_LABEL2 + " TEXT," +
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
                    DbaseEntry.COLUMN_NAME_LABEL2,
                    DbaseEntry.COLUMN_NAME_INFO,
                    DbaseEntry.COLUMN_NAME_LINK
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = "";
            for(int i=0; i<label.length-1;++i)
            {
                selection += DbaseEntry.COLUMN_NAME_LABEL + " = ? OR " ;
                selection += DbaseEntry.COLUMN_NAME_LABEL2 + " = ? OR " ;
            }
            selection += DbaseEntry.COLUMN_NAME_LABEL + " = ?";

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
            while(cursor.moveToNext()) {
                products.add(new Product(cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_IMAGE)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_LABEL)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(DbaseEntry.COLUMN_NAME_LABEL2)),
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
            List<Product> products = new ArrayList<>();

            products.add(new Product("https://my-test-11.slatic.net/p/6681aff3a5a7f8762965de8ccdaec11e.jpg","Mobile phone","Realme", "Realme5","Midrange specs phone. Released in the year 2019. Features a 6.3 inch screen, 4 cameras, the biggest being 48 megapixels, and a 5000 milli amp hour battery.","https://www.gsmarena.com/realme_5-9802.php"));
            products.add(new Product("https://i.ebayimg.com/00/s/NjQwWDY0MA==/z/1gQAAOSw7KddA7Fu/$_57.JPG?set_id=8800005007","Cereal","Chocolate","Koko Krunch","Koko Krunch is a chocolate-flavored whole-grain breakfast cereal distributed by Nestlé in most of Europe, Asia, the Middle-East and Latin America.","https://www.nestle-cereals.com/ph/en/products-promotions/brands/koko-krunch-brand"));
            products.add(new Product("https://my-live-01.slatic.net/original/22011b4a201f40d918bb57ca656a643b.jpg","Mobile phone","Apple","iPhone XR","Part of the refreshed X family. Features a 6.1 inch screen, 12 megapixel main camera, 7 megapixel front camera, and 2900 milli amp hour battery with fast charging.","https://www.gsmarena.com/apple_iphone_xr-9320.php"));
            products.add(new Product("https://images-na.ssl-images-amazon.com/images/I/81QpkIctqPL._AC_SL1500_.jpg","Monitor","Acer","Acer SB220Q","Acer SB220Q bi 21.5\" Full HD (1920 x 1080) IPS Ultra-Thin Zero Frame Monitor (HDMI & VGA Port) ","https://www.newegg.com/acer-sb220q-bi-21-5/p/0JC-000P-00AT1"));
            products.add(new Product("http://www.marigold.com.my/assets/images/contents/products/milk/hl-milk/showcase/low-fat-milk/strawberry/full.jpg","Carton","Strawberry","HL Strawb","MARIGOLD HL Low Fat Milk is more than just ordinary milk. It is high in calcium and protein while low in fat and lactose, also fortified with the 9 essential vitamins.","http://www.marigold.com.my/products/milk/hl-milk.html"));
            products.add(new Product("https://www.timex.com/dw/image/v2/BBDM_PRD/on/demandware.static/-/Sites-timex-master-catalog/default/dw278f3d05/images/TW2T32900.jpg?sw=900&sh=900&sm=fit&sfrm=png","Clock","Leather", "Timex Allied LT","Analog dial watch. 4.9 millimetres thin. Danish branded watch. Genuine leather strap","https://www.timex.com/allied-lt-chronograph-42mm-leather-strap-watch/Allied-LT-Chronograph-42mm-Leather-Strap-Watch.html?dwvar_Allied-LT-Chronograph-42mm-Leather-Strap-Watch_color=Silver-Tone-Brown-Black&cgid=men-leather#start=1"));
            products.add(new Product("https://www.obaku.com/content/watch/V230GXCBMC_PAPIR_ONYX.jpg","Clock","Metal", "Obaku Papir","Analog dial watch. 4.9 millimetres thin. Danish branded watch. Sapphire crystal mirror.","https://www.obaku.com/watch/papir-onyx"));
            products.add(new Product("https://my-test-11.slatic.net/p/1249e55443f3d3901359601f2035b6a9.jpg","Clock","Smartphone","Apple Watch 5","Glass front (Sapphire crystal), ceramic/sapphire crystal back, stainless steel frame","https://www.gsmarena.com/apple_watch_series_5-9859.php"));
            products.add(new Product("https://images-na.ssl-images-amazon.com/images/I/61SVEUsMepL._AC_SX522_.jpg","Mobile phone","Samsung","Samsung Galaxy A20","Part of the second gen A series. Features a 6.4 inch screen, dual rear camera with 13 megapixels, selfie camera of 8 megapixels, and a 4000 milli amp hour battery.","https://www.gsmarena.com/samsung_galaxy_a20-9640.php"));
            products.add(new Product("https://fdn2.gsmarena.com/vv/bigpic/motorola-razr-2019-.jpg","Mobile phone","Motorola","Motorola RAZR Fold","Folding screen smartphone, released in the year 2020. Features a 6.2 inch screen, 2 16 megapixel cameras on the back, and a 5 megapixel camera ","https://www.gsmarena.com/motorola_razr_2019-9630.php"));
            products.add(new Product("https://cf.shopee.com.my/file/3b7ccae307825d66d1b934353fa54393","Poster","Food","Kellogg Coco Pops","Delicious chocolate cereal, low in sugars and salt, small pieces of crunchy chocolate that provide nutrients.","https://www.kelloggs.co.uk/en_GB/brands/coco-pops-.html"));
            products.add(new Product("https://secure.ap-tescoassets.com/assets/MY/039/8852756304039/ShotType1_540x540.jpg","Poster","Food","Kellogg Coco Chex","Delicious Chocolate cereal, square pellets, rich in nutrients and Vitamin C and D","https://www.kelloggs.com.au/en_AU/products/coco-pops-chex-product.html"));


            for (Product p:products) {
                long newRowId = dbWrite.insert(TABLE_NAME, null, p.getContentValues());
                Log.d(TAG, "New row ID is :" + newRowId);

            }
        }
    }

}
