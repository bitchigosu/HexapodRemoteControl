package com.example.konstantin.hexapod;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konstantin.hexapod.data.AddressContract;
import com.example.kvlmjpeg.DisplayMode;
import com.example.kvlmjpeg.Mjpeg;
import com.example.kvlmjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StreamActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    @BindView(R.id.mjpegSurfaceView)
    MjpegView mjpegeView;

    private String URL;
    private int port;

    private String directionz;

    private final Handler handler=new Handler();

    private TextView textView5;
    private joystick js;
    private Uri uriData;
    private AddressCursorAdapter addressCursorAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        ButterKnife.bind(this);

        Intent intent=getIntent();
        uriData=intent.getData();
        addressCursorAdapter =new AddressCursorAdapter(this,null);
        getLoaderManager().initLoader(0,null,this);


        RelativeLayout layout_joystick;

        textView5 = (TextView)findViewById(R.id.textView5);
        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new joystick(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(115, 115);
        js.setLayoutSize(300, 300);
        js.setLayoutAlpha(150);
        js.setStickAlpha(150);
        js.setOffset(30);
        js.setMinimumDistance(50);


        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN ||
                        arg1.getAction() == MotionEvent.ACTION_MOVE ) {

                    int direction = js.get8Direction();
                    if (direction == joystick.STICK_UP) {
                        textView5.setText(getString(R.string.direction_up));
                        directionz="1";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_UPRIGHT) {
                        textView5.setText(getString(R.string.direction_upright));
                        directionz="2";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_RIGHT) {
                        textView5.setText(getString(R.string.direction_right));
                        directionz="3";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_DOWNRIGHT) {
                        textView5.setText(getString(R.string.direction_downright));
                        directionz="4";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_DOWN) {
                        textView5.setText(getString(R.string.direction_down));
                        directionz="5";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_DOWNLEFT) {
                        textView5.setText(getString(R.string.direction_downleft));
                        directionz="6";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_LEFT) {
                        textView5.setText(getString(R.string.direction_left));
                        directionz="7";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_UPLEFT) {
                        textView5.setText(getString(R.string.direction_upleft));
                        directionz="8";
                        handler.post(runnable);
                    } else if (direction == joystick.STICK_NONE) {
                        textView5.setText(getString(R.string.direction_unknown));
                        handler.removeCallbacksAndMessages(null);
                    }

                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView5.setText(getString(R.string.direction_unknown));
                    handler.removeCallbacksAndMessages(null);
                }
                return true;
            }
        });

    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            ClientTask ct=new ClientTask(URL,port);
            ct.execute(directionz);
            handler.postDelayed(runnable,4000);
            handler.removeCallbacksAndMessages(runnable);
        }
    };


    private void loadStream(){
        Mjpeg.newInstance().open("http://" + URL + ":" + port + "/axis-cgi/mjpg/video.cgi?resolution=352x288" /*"/?action=stream"*/, 5)
                .subscribe(
                        mjpegInputStream -> {
                            mjpegeView.setSource(mjpegInputStream);
                            mjpegeView.setDisplayMode(DisplayMode.BEST_FIT);
                            mjpegeView.showFps(true);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                        }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
       //TODO: loadStream();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegeView.stopPlayback();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BaseColumns._ID,
                AddressContract.AdressEntry.COLUMN_ADRESS,
                AddressContract.AdressEntry.COLUMN_PORT
        };
        return new CursorLoader(this,uriData,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if ((Cursor)data != null && ((Cursor) data).getCount() > 0) {
            ((Cursor) data).moveToFirst();
            int addressIndex = ((Cursor) data).getColumnIndex(AddressContract.AdressEntry.COLUMN_ADRESS);
            int portIndex = ((Cursor) data).getColumnIndex(AddressContract.AdressEntry.COLUMN_PORT);

            URL = ((Cursor) data).getString(addressIndex);
            String _port = ((Cursor) data).getString(portIndex);
            port = Integer.parseInt(_port);

            loadStream();
        }

        addressCursorAdapter.swapCursor((Cursor) data);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        addressCursorAdapter.swapCursor(null);
    }
}

