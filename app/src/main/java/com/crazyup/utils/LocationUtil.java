package com.crazyup.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.crazyup.app.CrazyApplication;

import java.util.Date;

/**
 * Created by jeongmin on 17. 4. 21.
 */

public class LocationUtil {

    private static final String TAG = "LocationUtil";

    private static LocationUtil _inst = null;

    private float longitude = 0;
    private float latitude = 0;
    private Context context;

    public static LocationUtil inst() {
        if (_inst == null) {
            _inst = new LocationUtil();
//            Log.d(TAG, "inst(), _inst="+_inst);
        }
        return _inst;
    }

    private LocationUtil() {
    }


    public void reqLocation(Context context) {
        Log.d(TAG, "reqLocation(),");
        this.context = context;
        this.longitude = 0;
        this.latitude = 0;
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
//        List<String> plist = locationManager.getAllProviders();
//        for (String pname: plist) {
//            Log.d(TAG, "onCheckLocationPerm(), pname="+pname);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    && (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
                }
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);
                }
                if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500, 1, locationListener);
                }
            }
        } else {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);
            }
            if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 500, 1, locationListener);
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged(), location="+location);

            LocationManager locationManager = (LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);

            longitude = (float) location.getLongitude();
            latitude = (float) location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged(), status="+status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled(), provider="+provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled(), provider="+provider);
        }
    };


    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }
}
