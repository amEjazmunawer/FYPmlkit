package com.google.firebase.samples.apps.mlkit.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static androidx.camera.core.CameraX.getContext;

public final class DatabaseLite {

    private DatabaseLite() {}

    public static class DbaseEntry implements BaseColumns {

        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";

    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbaseEntry.TABLE_NAME + " (" +
                    DbaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DbaseEntry.COLUMN_NAME_TITLE + " TEXT," +
                    DbaseEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbaseEntry.TABLE_NAME;

    public class FeedReaderDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

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




}
