package com.ks.poc.whereiam;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
                if (current == null || l.getAccuracy() < current.getAccuracy()) {
                    // Found best last known location: %s", l);
                    current = l;
                    provider_info = provider;
                }
            }

            Log.d(TAG, "Successfully get current location via " + provider_info + "(" + current.getLatitude() + ", " + + current.getLongitude() + ")");
            return current;

        } else {
            Log.d(TAG, "Location Service is not available");
            return null;
        }
    }
}
