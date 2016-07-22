package com.ks.poc.whereiam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Krit on 7/13/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "WhereIam";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // This callback method will be called only if app is running in foreground. If not, the message will be displayed on Android's notification area only.
        // To always receive this callback regardless of app in background or not running at all, send message with "body" only (no "notification").

        Log.d(TAG, "Message From: " + remoteMessage.getFrom());

        String msgType;

        if (remoteMessage.getData().containsKey("MsgType")) {
            msgType = remoteMessage.getData().get("MsgType");
            if (msgType.equalsIgnoreCase("R")) {
                sendLocation();
            } else if (msgType.equalsIgnoreCase("T")) {
                receiveLocationInfo(remoteMessage);
            } else {
                Log.d(TAG, "Invalid MsgType(" + msgType + ")");
            }
        } else {
            Log.d(TAG, "Invalid Notification Message. Missing MsgType");
        }


        //Displaying data in log
        //It is optional
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //Calling method to generate notification
//        sendNotification(remoteMessage.getNotification().getBody());
//        sendNotification("Hello");
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String dateTime, String who, String Lat, String Lng) {
        Intent intent = new Intent(this, LocationMap.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DATETIME",dateTime);
        Location loc = new Location("");
        loc.setLatitude(Double.parseDouble(Lat));
        loc.setLongitude(Double.parseDouble(Lng));
        intent.putExtra("LOCATION",loc);
        intent.putExtra("WHO", who);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Location Received")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendLocation() {
        GPSService locService = GPSService.getInstance();
        Location currentLoc = locService.getLatLong(this);
        if (currentLoc != null) {
            Log.d(TAG, "Location: " + currentLoc.getLatitude() + ", " + currentLoc.getLongitude());

            String message;
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = formatter.format(new Date());
            String kai_key = "cdqFiSFRtGs:APA91bF9Rh8f8Kg-93Hphp4TqXLzhAALktskRKN_jNfF4jJFthTuB5LpsGs3tDyrdTUPpbrOvxvEg6YitFN4-sv7i7SFHUBqb1I5tunqrV5BTbdhMteSws8MdtbGxaWwFvkQHTvxRTJM";

            message = "{\"MsgType\":\"T\",\"WHO\":\"" + Build.MODEL + "\",\"DATETIME\":\"" + dateTime + "\",\"LAT\":\"" + currentLoc.getLatitude() + "\",\"LNG\":\"" + currentLoc.getLongitude() + "\"}";
            Log.d(TAG, "Sending Notification with Data: " + message);

            FCMDownstreamMessage messenger = new FCMDownstreamMessage();
            messenger.execute(kai_key, message);
            String result;
            try {
                result = messenger.get();
            } catch (Exception e) {
                result = e.getMessage();
            }

            Log.d(TAG, "Sending Notification Result: " + currentLoc.getLatitude() + ", " + currentLoc.getLongitude());
        } else {
            Log.d(TAG, "Cannot acquire current location");
        }
    }

    private void receiveLocationInfo(RemoteMessage remoteMessage) {
        String dateTime;
        String who;
        String lat;
        String lng;

        who = remoteMessage.getData().get("WHO");
        dateTime = remoteMessage.getData().get("DATETIME");
        lat = remoteMessage.getData().get("LAT");
        lng = remoteMessage.getData().get("LNG");
        Log.d(TAG, "Tracking message received: " + who + ", " + dateTime + ", " + lat + ", " + lng);
        sendNotification("Tracking message received: " + who + ", " + dateTime + ", " + lat + ", " + lng,dateTime, who,lat,lng);
    }
}