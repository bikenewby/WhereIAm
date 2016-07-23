package com.ks.poc.whereiam;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static String TAG = MainActivity.class.getName();
    public static boolean hasPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.d(this.getClass().getName(), "Token: " + token);
        } else {
            Toast.makeText(MainActivity.this, "No token found", Toast.LENGTH_SHORT).show();
        }
    }

    public void submitClicked(View view) {
        GPSService locService = GPSService.getInstance();
        Location currentLoc = locService.getLatLong(this);
        if (currentLoc != null) {
            Toast.makeText(MainActivity.this, "(" + currentLoc.getLatitude() + ", " + currentLoc.getLongitude() + ")", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LocationMap.class);
            intent.putExtra("LOCATION",currentLoc);
            intent.putExtra("WHO", Build.MODEL + " (" + currentLoc.getProvider() + "/" + currentLoc.getAccuracy() + "/" + age_second(currentLoc) + ")");

            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = formatter.format(new Date());

            intent.putExtra("DATETIME", dateTime);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Location Service not available", Toast.LENGTH_LONG).show();
        }
    }

    private long age_second(Location last) {
        return ((SystemClock.elapsedRealtimeNanos() - last.getElapsedRealtimeNanos()) / 1000000) / 1000;
    }

    public void sendLocClicked(View view) {
        String message;

        message = "{\"MsgType\":\"R\"}";
        String nuch_key = "ePZcZwm0adU:APA91bGE9mvQcofvnnQS0I4M1s2abHNptjmJI14ZwCxB_-FjCWdSDKOpUd-UmmuPJdKIGd1zdMv7CWzatbC_HrlqdyNGjKjdtVYwcQMdcb_CWa9E-8vY_p2nvckzgHoyXj56zQXhhYnm";

        FCMDownstreamMessage messenger = new FCMDownstreamMessage();
        messenger.execute(nuch_key, message);
        String result;
        try {
            result = messenger.get();
        } catch (Exception e) {
            result = e.getMessage();
        }
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
    }

    private  boolean checkAndRequestPermissions() {
        int permissionAccessFineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionInternetAccess = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionInternetAccess != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "INTERNET & location services permission granted");
                        hasPermission = true;
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    }
                }
            }
        }

    }
}
