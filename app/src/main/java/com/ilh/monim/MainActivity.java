package com.ilh.monim;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    static Boolean M = false;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;
   // static  WebView webview = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //http://62.90.212.13:88/safari2.htm?user=admin&pass=27321496
        // Create a matrix for the scaling and add the scaling data
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button dayButton = findViewById(R.id.button2);
        dayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendgetrewquest(new URL("http://81.218.182.114:8080/set_alarm.cgi?user=admin&pwd=27321496&next_url=alarm_success.htm&motion_armed=1&motion_sensitivity=0&motion_compensation=1&sounddetect_armed=0&sounddetect_sensitivity=0&iolinkage=0&mail=1&upload_interval=2&schedule_enable=1&schedule_sun_0=4194288&schedule_sun_1=16777200&schedule_sun_2=0&schedule_mon_0=4194288&schedule_mon_1=16777200&schedule_mon_2=0&schedule_tue_0=4194288&schedule_tue_1=16777200&schedule_tue_2=0&schedule_wed_0=4194288&schedule_wed_1=16777200&schedule_wed_2=0&schedule_thu_0=4194288&schedule_thu_1=16777200&schedule_thu_2=0&schedule_fri_0=4194288&schedule_fri_1=0&schedule_fri_2=0&schedule_sat_0=4194288&schedule_sat_1=0&schedule_sat_2=0 HTTP/1.1\n"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button nightButton = findViewById(R.id.button);
        nightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendgetrewquest(new URL("http://81.218.182.114:8080/set_alarm.cgi?user=admin&pwd=27321496&next_url=alarm_success.htm&motion_armed=1&motion_sensitivity=0&motion_compensation=1&sounddetect_armed=0&sounddetect_sensitivity=0&iolinkage=0&mail=1&upload_interval=2&schedule_enable=1&schedule_sun_0=4194288&schedule_sun_1=0&schedule_sun_2=0&schedule_mon_0=4194288&schedule_mon_1=0&schedule_mon_2=0&schedule_tue_0=4194288&schedule_tue_1=0&schedule_tue_2=0&schedule_wed_0=4194288&schedule_wed_1=0&schedule_wed_2=0&schedule_thu_0=4194288&schedule_thu_1=0&schedule_thu_2=0&schedule_fri_0=4194288&schedule_fri_1=0&schedule_fri_2=0&schedule_sat_0=4194288&schedule_sat_1=0&schedule_sat_2=0\n"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        final Button allButton = findViewById(R.id.button3);
        allButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendgetrewquest(new URL("http://81.218.182.114:8080/set_alarm.cgi?user=admin&pwd=27321496&next_url=alarm_success.htm&motion_armed=1&motion_sensitivity=0&motion_compensation=1&sounddetect_armed=0&sounddetect_sensitivity=0&iolinkage=0&mail=1&upload_interval=2&schedule_enable=1&schedule_sun_0=-1&schedule_sun_1=-1&schedule_sun_2=-1&schedule_mon_0=-1&schedule_mon_1=-1&schedule_mon_2=-1&schedule_tue_0=-1&schedule_tue_1=-1&schedule_tue_2=-1&schedule_wed_0=-1&schedule_wed_1=-1&schedule_wed_2=-1&schedule_thu_0=-1&schedule_thu_1=-1&schedule_thu_2=-1&schedule_fri_0=-1&schedule_fri_1=-1&schedule_fri_2=-1&schedule_sat_0=-1&schedule_sat_1=-1&schedule_sat_2=-1 HTTP/1.1\n"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SetImage("http://81.218.182.114:8080/snapshot.cgi?user=admin&pwd=27321496");
                ImageView i = (ImageView)findViewById(R.id.MoninView);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                matrix.reset();
                M=false;
            }
        });



        SetImage("http://81.218.182.114:8080/snapshot.cgi?user=admin&pwd=27321496");
        reload();
    }

    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SetImage("http://81.218.182.114:8080/snapshot.cgi?user=admin&pwd=27321496");
                // Do something after 5s = 5000ms
                reload();
            }
        }, 1000);
    }

    public void SetImage(String imageUrl) {
        try {
            ImageView i = (ImageView)findViewById(R.id.MoninView);

           i.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub

                    ImageView view = (ImageView) v;

                    // Handle touch events here...
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (M==false) view.setScaleType(ImageView.ScaleType.MATRIX);
                            M=true;
                            savedMatrix.set(matrix);
                            start.set(event.getX(), event.getY());
                            mode = DRAG;

                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event);
                            if (oldDist > 10f) {
                                savedMatrix.set(matrix);
                                midPoint(mid, event);
                                mode = ZOOM;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                // ...
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - start.x, event.getY()
                                        - start.y);
                            } else if (mode == ZOOM) {
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }
                            }
                            break;
                    }

                    view.setImageMatrix(matrix);
                    return true;
                }
            });
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            i.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void sendgetrewquest(URL url)
    {
        HttpURLConnection urlConnection = null;

        try {

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                Snackbar.make(findViewById(R.id.MoninView), current,
                        Snackbar.LENGTH_SHORT)
                        .show();
               // System.out.print(current);
            }
        } catch (Exception e) {

            Snackbar.make(findViewById(R.id.MoninView), e.getMessage(),
                    Snackbar.LENGTH_SHORT)
                    .show();
           e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

}
