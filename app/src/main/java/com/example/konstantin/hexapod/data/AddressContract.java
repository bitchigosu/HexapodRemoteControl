package com.example.konstantin.hexapod.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class AddressContract {

    public static final String CONTENT_AUTHORITY = "com.example.konstantin.hexapod";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ADRESS = "hexapod";

    public static abstract class AdressEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"+ PATH_ADRESS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"+ PATH_ADRESS;

        public static final String TABLE_NAME = "adress";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_ADRESS = "adress";
        public static final String COLUMN_PORT = "port";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_ADRESS);


    }
}
