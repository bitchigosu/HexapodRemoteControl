package com.example.konstantin.hexapod.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.konstantin.hexapod.data.AddressContract.AdressEntry;

public class AddressDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME= "Adress.db";

    public AddressDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + AdressEntry.TABLE_NAME
                + " (" + AdressEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AdressEntry.COLUMN_ADRESS + " TEXT NOT NULL, "
                + AdressEntry.COLUMN_PORT + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
