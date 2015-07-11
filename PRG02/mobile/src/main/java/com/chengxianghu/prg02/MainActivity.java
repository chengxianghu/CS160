package com.chengxianghu.prg02;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

import android.content.Intent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import twitter4j.*;
import java.util.List;



public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VWFld4sw6COdJBemGimZMW8TZ";
    private static final String TWITTER_SECRET = "9hpoDZY7d3cCaj3CrD5cNJBuLmEINnNNogciZJikZkKHNwPf6P";
    private TwitterLoginButton loginButton;

    private static final String TAG = "mobile_MainActivity";
    private String mCurrentPhotoPath = null;
    private Uri mCurrentPhotoUri = null;

    private Bitmap bmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        // Twitter
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
//                // Do something with result, which provides a TwitterSession for making API calls
//                Intent wearIntent = new Intent(this, MyMobileSenderService.class);
//                startService(wearIntent);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("openCam"));

        //for composing tweet
        Fabric.with(this, new TweetComposer());

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //  ... react to local broadcast message
            Log.d(TAG, "Received local broadcase...opening cam..");
            dispatchTakePictureIntent();
        }
    };

    private String tweetUrlStr = null;
    private MediaEntity[] media = null;
    private String tweetUserName = null;

    // Fetch Tweet
    private void fetchTweet() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService searchService = twitterApiClient.getSearchService();
        searchService.tweets("#cs160excited", null, null, null, "recent", 5, null, null, null, true, new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                final List<Tweet> tweets = result.data.tweets;
                new Thread() {
                    @Override
                    public void run() {
                        //Do something with result, which provides a Tweet inside of result.data
                        String url = null;
                        for (Tweet tweet : tweets) {
                            if (tweet.entities.media == null) {
                                continue;
                            } else {
                                tweetUserName = tweet.user.name;
                                url = tweet.entities.media.get(0).mediaUrl;
                                break;
                            }
                        }
                        try {
                            URL imageUrl = new URL(url);
                            Bitmap bmp = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                            createWearNotification(bmp);
                        } catch (IOException e) {
                        }
                    }
                }.start();
            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Log.d("url", "wrong");
            }
        });

//        twitter4j.Twitter twitter = new TwitterFactory().getInstance();
//        try {
//            twitter4j.Query query = new twitter4j.Query("#cs160excited");
//            query.setResultType(Query.RECENT);
//            //Query query = new Query("#cs160excited");
//            QueryResult result;
//            do {
//                result = twitter.search(query);
//                List<Status> tweets = result.getTweets();
//                Log.d(TAG, ".........looping others' tweet");
//
//                for (Status tweet : tweets) {
//                    // System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//                    tweet.getMediaEntities();
//                    tweetUser = tweet.getUser().getScreenName();
//                    tweetContent = tweet.getText();
//                    for (MediaEntity m: media) {
//                        tweetUrlStr = m.getMediaURL();
//                        break;
//                    }
//                    break;
//                }
//
//                Log.d(TAG, ".........Fetch others'tweet");
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            InputStream in = new URL(tweetUrlStr).openStream();
//                            bmp = BitmapFactory.decodeStream(in);
//                        } catch (Exception e) {
//                            // log error
//                        }
//                        if (bmp != null)
//                            Log.d(TAG, ".........Got others' tweet image");
//                            createWearNotification(bmp);
//                    }
//                }.start();

//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            InputStream in = new URL(tweetUrlStr).openStream();
//                            bmp = BitmapFactory.decodeStream(in);
//                        } catch (Exception e) {
//                            // log error
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        if (bmp != null)
//                            Log.d(TAG, ".........Got others' tweet image");
//                            //createWearNotification(bmp);
//                            //imageView.setImageBitmap(bmp);
//                    }
//
//                }.execute();
//
//            } while ((query = result.nextQuery()) != null);
//            System.exit(0);
//        } catch (TwitterException te) {
//            te.printStackTrace();
//            System.out.println("Failed to search tweets: " + te.getMessage());
//            System.exit(-1);
//        } catch (twitter4j.TwitterException e) {
//            e.printStackTrace();
//        }
    }

    protected void createWearNotification(Bitmap image){
        int notificationId = 2;
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, mainIntent, 0);
        // Build an intent

        Log.v(TAG, "Start build others' notification");

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();
        extender.setBackground(image);
        extender.setHintHideIcon(true);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.hero)
                        .setContentTitle("Someone else!!")
                        .setContentIntent(viewPendingIntent);
        notificationBuilder.extend(extender);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        notificationBuilder.setContentText(tweetUserName + " is also excited!");
        notificationBuilder.setLargeIcon(image);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
        //
        Log.v(TAG, "Other's tweet Notication Built");
    }

    // Compose Tweet
    private void makeTweet() throws IOException {
        if (mCurrentPhotoPath == null) {
            Log.d(TAG, "image path is null");
        } else {
            Intent tweetIntent = new TweetComposer.Builder(this)
                    .text("#cs160excited")
                    .image(mCurrentPhotoUri)
                    .createIntent();
            startActivityForResult(tweetIntent, 10); // 10 requestCode, can be any
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "There is a camera");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                mCurrentPhotoUri = Uri.fromFile(photoFile);
                Log.d(TAG, "Open Camera!!");
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + "/" + image.getAbsolutePath();
        mCurrentPhotoPath =  mCurrentPhotoPath.substring(0,mCurrentPhotoPath.length() - 4);

        Log.d(TAG, "image path is..." + mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                makeTweet();
                Log.d(TAG, "Successfully made tweet");
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to make tweet");
            e.printStackTrace();
        }

        if (requestCode == 10) {
            Log.d(TAG, "Send Tweet Button Clicked");
            fetchTweet();
            // Intent wearIntent = new Intent(this, MyMobileSenderService.class);
            //startService(wearIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
