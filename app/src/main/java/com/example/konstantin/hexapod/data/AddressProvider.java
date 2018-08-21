package com.example.konstantin.hexapod.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.konstantin.hexapod.data.AddressContract.AdressEntry;

public class AddressProvider extends ContentProvider {

    public static final String LOG_TAG = AddressProvider.class.getSimpleName();
    private static final int ADRESS = 100;
    private static final int ADRESS_ID = 101;

    private static final UriMatcher sUriMathcer = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMathcer.addURI(AddressContract.CONTENT_AUTHORITY, AddressContract.PATH_ADRESS, ADRESS);
        sUriMathcer.addURI(AddressContract.CONTENT_AUTHORITY, AddressContract.PATH_ADRESS + "/#",ADRESS_ID);
    }

    private AddressDbHelper adressDbHelper;


    @Override
    public boolean onCreate() {
        adressDbHelper=new AddressDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = adressDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMathcer.match(uri);
        switch (match) {
            case ADRESS:
                cursor=database.query(AdressEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ADRESS_ID:
                selection = AdressEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AdressEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMathcer.match(uri);
        switch (match) {
            case ADRESS:
                return insertAdress(uri,values);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertAdress(Uri uri, ContentValues values) {

        SQLiteDatabase database=adressDbHelper.getWritableDatabase();
        String address = values.getAsString(AdressEntry.COLUMN_ADRESS);
        if (address == null)
            throw new IllegalArgumentException("Server requires an adress");

        Integer port = values.getAsInteger(AdressEntry.COLUMN_PORT);
        if (port < 0 || port == null)
            throw new IllegalArgumentException("Server requires a valid port");

        long id = database.insert(AdressEntry.TABLE_NAME,
                null,
                values);
        if (id==-1) {
            Log.e(LOG_TAG,"Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMathcer.match(uri);
        switch (match) {
            case ADRESS:
                return deleteAddress(uri,selection,selectionArgs);
            case ADRESS_ID:
                selection = AdressEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deleteAddress (uri, selection,selectionArgs);
                default:
                    throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    private int deleteAddress(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = adressDbHelper.getWritableDatabase();
        int rowsDeleted = database.delete(AdressEntry.TABLE_NAME,selection,selectionArgs);
        if (rowsDeleted !=0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMathcer.match(uri);
        switch (match) {
            case ADRESS:
                return updateAddress(uri, values, selection, selectionArgs);
            case ADRESS_ID:
                selection=AdressEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateAddress(uri, values, selection,selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateAddress(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(AdressEntry.COLUMN_ADRESS)) {
            String address = values.getAsString(AdressEntry.COLUMN_ADRESS);
            if (address==null)
                throw new IllegalArgumentException("Server requires an address");
        }
        if (values.containsKey(AdressEntry.COLUMN_PORT)) {
            Integer port = values.getAsInteger(AdressEntry.COLUMN_PORT);
            if (port < 0 || port==null)
                throw new IllegalArgumentException("Server requires a valid port");
        }

        if (values.size()==0)
            return 0;

        SQLiteDatabase database=adressDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(AdressEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
}
