package com.chengxianghu.prg02;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chengxianghu on 7/7/15.
 */
public class MyReceiverService extends WearableListenerService {

    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private static final String TAG = "MyReceiverService";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Got a message");
        camMessageToActivity();
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
