package com.oceanscan.sunshine.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.oceanscan.sunshine.data.SunshinePreferences;

import java.util.logging.Logger;

import static com.oceanscan.sunshine.utils.Constants.LOCATION_UPDATE_MIN_DISTANCE;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_UPDATE_MIN_TIME;

public class LocationService extends JobService {

     LocationManager locationManager;
    private static String TAG = LocationService.class.getSimpleName();
     Context context;

    @Override
    public boolean onStartJob(final JobParameters params) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
context=LocationService.this;
        getCurrentLocation();
        jobFinished(params, false);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }

    public   void getCurrentLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Toast.makeText(context, "GPS and Network not enabled", Toast.LENGTH_LONG).show();
            ///  Snackbar.make(mMapView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
        else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {

            SunshinePreferences.setMyLocationDetails(context,location.getLatitude(),
                    location.getLongitude());

            Log.i(TAG, String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()));

        }
    }

    private  LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.i(TAG, String.format("%f, %f", location.getLatitude(), location.getLongitude()));

                locationManager.removeUpdates(mLocationListener);
            } else {
                Log.i(TAG, "Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}