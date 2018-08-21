package com.example.konstantin.hexapod;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.konstantin.hexapod.data.AddressContract;

public class catalog_activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    AddressCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_catalog_activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(catalog_activity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView addressListView = (ListView) findViewById(R.id.list_view);
        cursorAdapter = new AddressCursorAdapter(this,null);
        addressListView.setAdapter(cursorAdapter);

        registerForContextMenu(addressListView);

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(catalog_activity.this,StreamActivity.class);
                Uri uri = ContentUris.withAppendedId(AddressContract.AdressEntry.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        addressListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(catalog_activity.this,EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(AddressContract.AdressEntry.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
                return true;
            }
        });

        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.options,menu);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BaseColumns._ID,
                AddressContract.AdressEntry.COLUMN_ADRESS,
                AddressContract.AdressEntry.COLUMN_PORT
        };
        return new CursorLoader(this, AddressContract.AdressEntry.CONTENT_URI,projection, null,null,null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        cursorAdapter.swapCursor((Cursor) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        cursorAdapter.swapCursor(null);

    }
}
