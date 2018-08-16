package com.example.konstantin.dadad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kvlmjpeg.DisplayMode;
import com.example.kvlmjpeg.Mjpeg;
import com.example.kvlmjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StreamActivity extends AppCompatActivity {

    @BindView(R.id.mjpegSurfaceView)
    MjpegView mjpegeView;

    private String URL, URLData;
    private int port;

    private String directionz;

    private final Handler handler=new Handler();

    private TextView textView5;
    private joystick js;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        ButterKnife.bind(this);
        URL=getIntent().getStringExtra("ip");
        URLData=getIntent().getStringExtra("ipdata");
        port=getIntent().getIntExtra("port",8080);
        loadStream();

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
                if(arg1.getAction() == MotionEvent.ACTION_DOWN) {

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
            ClientTask ct=new ClientTask(URLData,port);
            ct.execute(directionz);
            handler.postDelayed(runnable,4000);
            handler.removeCallbacksAndMessages(runnable);
        }
    };


    private void loadStream(){
        Mjpeg.newInstance().open(URL+":"+port+"/?action=stream", 5)
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
        loadStream();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegeView.stopPlayback();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
    }


}

