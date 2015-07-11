package com.chengxianghu.prg02;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Sense imports
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
    private GoogleApiClient mGoogleApiClient;
    public static final String CAPABILITY_NAME = "do_stuff";
    public static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private String nodeId = null;
    private TextView mTextView;
    private static final String TAG = "MyActivity";

    public static final String ACTION_DEMAND = "com.chengxianghu.ACTION_DEMAND";
    public static final String EXTRA_MESSAGE = "com.chengxianghu.EXTRA_MESSAGE";
    public static final String EXTRA_TWITTER_REPLY = "com.chengxianghu.EXTRA_TWITTER_REPLY";


    private float mLastX, mLastY, mLastZ;

    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 2.0;

    // Accelerometer variables
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    public NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sensor
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        finish();
    }

    // Sensor Methods
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // Create notification when the sensor detects rapid movement
                    Log.v(TAG, "Shaking...");

                    int notificationId = 001;

//                    Intent viewIntent = new Intent(this, MainActivity.class);
//                    PendingIntent viewPendingIntent =
//                            PendingIntent.getActivity(this, 1000, viewIntent, 0);

                    Intent camIntent = new Intent(this, MySenderService.class)
                                                    .putExtra(EXTRA_MESSAGE, "Reply Selected")
                                                    .setAction(ACTION_DEMAND);
                    PendingIntent camPendingIntent = PendingIntent.getService(this, 0, camIntent, 0);
                    //.........getService or getActivity?


                    Bitmap backImage = BitmapFactory.decodeResource(getResources(), R.drawable.excited);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(MainActivity.this)
                                    .setSmallIcon(R.drawable.ic_camera_enhance_black_48dp)
                                    .setContentTitle("Excited?!")
                                    .setContentText("Take a Photo :D")
                                    .extend(new NotificationCompat.WearableExtender().setBackground(backImage))
                                            //.setContentIntent(camPendingIntent)
                                    .addAction(R.drawable.ic_camera_enhance_black_48dp, "Open Camera", camPendingIntent);

                    // Get an instance of the NotificationManager service
                    notificationManager =
                            NotificationManagerCompat.from(this);
                    // Build the notification and issues it with notification manager.
                    notificationManager.notify(notificationId,
                            notificationBuilder.build());
                    Log.v(TAG, "Notification build completed");

                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
