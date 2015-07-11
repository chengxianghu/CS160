package com.chengxianghu.prg02;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by chengxianghu on 7/9/15.
 */
public class MyWearReceiverService extends WearableListenerService {
    private static final String RECEIVER_SERVICE_PATH = "/wear-receiver-service";
    private static final String TAG = "MyWearReceiverService";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {


        Log.d(TAG, "Message from mobile get");
        buildNotification();
    }

    public void buildNotification() {
        int notificationId = 2;

//                    Intent viewIntent = new Intent(this, MainActivity.class);
//                    PendingIntent viewPendingIntent =
//                            PendingIntent.getActivity(this, 1000, viewIntent, 0);
//
//        Intent camIntent = new Intent(this, MySenderService.class);
//        PendingIntent camPendingIntent = PendingIntent.getService(this, 0, camIntent, 0);

        Log.v(TAG, "Twitter Notification Start");


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(MyWearReceiverService.this)
                        .setSmallIcon(R.drawable.ic_camera_enhance_black_48dp)
                        .setContentTitle("Twitter")
                        .setContentText("Tweet Sent");
                                //.setContentIntent(camPendingIntent)
                        //.addAction(R.drawable.ic_camera_enhance_black_48dp, "Open Camera", camPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId,
                notificationBuilder.build());
        Log.v(TAG, "Twitter Notification Build Completed");
    }

    private void camMessageToActivity() {
        Intent intent = new Intent("openCam");
        sendLocationBroadcast(intent);
    }

    private void sendLocationBroadcast(Intent intent){
        // intent.putExtra("camMessage", camMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
