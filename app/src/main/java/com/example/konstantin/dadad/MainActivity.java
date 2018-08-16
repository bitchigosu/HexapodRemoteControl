package com.example.konstantin.dadad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.buttonStream)
    Button buttonStream;

    private SharedPreferences prefs;

    private String savedText;
    private final String SAVED_TEXT = "saved_text";

    private String savedPort;
    private final String SAVED_PORT = "saved_port";

    private EditText editTextIP;
    private EditText editTextPort;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        editTextIP = (EditText) findViewById(R.id.editText);
        editTextPort = (EditText) findViewById(R.id.editTextPort);

        loadtext();
    }


    private void saveText() {
        prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SAVED_TEXT, editTextIP.getText().toString());
        editor.putString(SAVED_PORT,editTextPort.getText().toString());
        savedText = editTextIP.getText().toString();
        savedPort=editTextPort.getText().toString();
        editor.commit();
    }

    private void loadtext() {
        prefs = getPreferences(MODE_PRIVATE);
        savedText = prefs.getString(SAVED_TEXT, "");
        savedPort=prefs.getString(SAVED_PORT,"");
        editTextIP.setText(savedText);
        editTextPort.setText(savedPort);
    }

    @OnClick(R.id.buttonStream)
    public void onClickStream() {
        saveText();

        if (!(savedText.toLowerCase().startsWith("http://") || savedText.toLowerCase().startsWith("https://")))
            savedText = "http://" + savedText;
        startActivity(new Intent(this, StreamActivity.class)
                .putExtra("ip", savedText)
                .putExtra("ipdata",editTextIP.getText().toString())
                .putExtra("port",Integer.parseInt(editTextPort.getText().toString())));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveText();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}



