package com.example.konstantin.hexapod;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AddressCursorAdapter extends CursorAdapter {
    public AddressCursorAdapter(Context context, Cursor c) {
        super(context,c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvIp = (TextView)view.findViewById(R.id.ipTextView);
        TextView tvPort = (TextView)view.findViewById(R.id.portTextView);

        String adress = cursor.getString(cursor.getColumnIndex("adress"));
        String port = cursor.getString(cursor.getColumnIndex("port"));

        tvIp.setText(adress);
        tvPort.setText(port);
    }
}
