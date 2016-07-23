package com.ks.poc.whereiam;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Krit on 7/20/2016.
 */
public class GPSService {
    private static GPSService ourInstance = new GPSService();
    private static String TAG = GPSService.class.getName();

    public static GPSService getInstance() {
        return ourInstance;
    }

    private GPSService() {
    }

    public Location getLatLong(Context mContext) {
        Location current;
        LocationManager locManager;
        String provider_info = "";

        locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                // No permission to access location service
                Log.d(TAG, "No permission to access location service.");
                return null;
            }

            List<String> providers = locManager.getProviders(true);
            current = null;
            for (String provider : providers) {
                Location l = locManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                Log.d(TAG, "LastKnownLocation: " + l.getProvider() + " (" + l.getLatitude() + ", " + l.getLongitude() + ") " + age_second(l) + " seconds, accuracy " + l.getAccuracy() + " meters.");
//                if (current == null || l.getAccuracy() < current.getAccuracy()) {
                if (current == null) {
                    current = l;
                    provider_info = provider;
                } else if (age_second(l) <= age_second(current)) { //l is newer
                    if (l.getAccuracy() < current.getAccuracy()) { //l is more accurate than current
                        current = l;
                        provider_info = provider;
                        } else if (age_second(current) > 180) { // current is last update for more than 3 mins
                        current = l;
                        provider_info = provider;
                    } // otherwise use current location
                }
//                if (current == null || (l.getElapsedRealtimeNanos() >= current.getElapsedRealtimeNanos() && l.getAccuracy() != 0f)) {
//                    // Found best last known location: %s", l);
//                    current = l;
//                    provider_info = provider;
//                }
            }

            Log.d(TAG, "Successfully get current location via " + provider_info + "(" + current.getLatitude() + ", " + + current.getLongitude() + ")");
            return current;

        } else {
            Log.d(TAG, "Location Service is not available");
            return null;
        }
    }

    private long age_second(Location last) {
        return ((SystemClock.elapsedRealtimeNanos() - last.getElapsedRealtimeNanos()) / 1000000) / 1000;
    }
}
