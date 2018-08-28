package com.example.konstantin.hexapod;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.konstantin.hexapod.data.AddressContract.AdressEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private EditText editTextIP;
    private EditText editTextPort;

    private AddressCursorAdapter cursorAdapter;
    private Uri addressData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent=getIntent();
        addressData =intent.getData();
        if (addressData ==null) {
            setTitle(getString(R.string.new_adress_title));
        }
        else {
            getLoaderManager().initLoader(0,null,this);
            setTitle(getString(R.string.edit_address_title));
        }

        cursorAdapter= new AddressCursorAdapter(this,null);

        editTextIP = (EditText) findViewById(R.id.editText);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveAdress();
                finish();
                return true;
            case R.id.action_delete:
                deleteAddress();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void saveAdress() {
        ContentValues values = new ContentValues();
        values.put(AdressEntry.COLUMN_ADRESS, editTextIP.getText().toString().trim());
        values.put(AdressEntry.COLUMN_PORT, Integer.parseInt(editTextPort.getText().toString()));

        if (addressData ==null) {
            Uri newUri = getContentResolver().insert(AdressEntry.CONTENT_URI,values);
            if (newUri == null)
                Toast.makeText(this, "Error with saving pet", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, " Adress is saved ", Toast.LENGTH_LONG).show();
        } else {
            int updatedId=getContentResolver().update(addressData,values,null,null);
            if (updatedId == 0)
                Toast.makeText(this, "Error with updating pet", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Adress is updated ", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAddress() {
        int deleteId = getContentResolver().delete(addressData,null,null);
        if (deleteId==0)
            Toast.makeText(this,"Address hasnt been deleted",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Address has been deleted",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BaseColumns._ID,
                AdressEntry.COLUMN_ADRESS,
                AdressEntry.COLUMN_PORT
        };
        return new CursorLoader(this, addressData,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if ((Cursor)data !=null && ((Cursor)data).getCount() > 0) {
            ((Cursor) data).moveToFirst();

            int addressIndex=((Cursor) data).getColumnIndex(AdressEntry.COLUMN_ADRESS);
            int portIndex = ((Cursor) data).getColumnIndex(AdressEntry.COLUMN_PORT);

            String address = ((Cursor) data).getString(addressIndex);
            String port = ((Cursor) data).getString(portIndex);

            editTextIP.setText(address);
            editTextPort.setText(port);
        }

        cursorAdapter.swapCursor((Cursor) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        editTextIP.setText("");
        editTextPort.setText("");
        cursorAdapter.swapCursor(null);

    }

}



