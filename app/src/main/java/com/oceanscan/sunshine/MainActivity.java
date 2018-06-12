package com.oceanscan.sunshine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.oceanscan.sunshine.adapter.ForecastAdapter;
import com.oceanscan.sunshine.broadcast.ConnectivityBroadcast;
import com.oceanscan.sunshine.data.SunshinePreferences;
import com.oceanscan.sunshine.data.WeatherContract;
import com.oceanscan.sunshine.service.LocationService;
import com.oceanscan.sunshine.sync.SunshineSyncTask;
import com.oceanscan.sunshine.sync.SunshineSyncUtils;
import com.oceanscan.sunshine.utils.Constants;
import com.oceanscan.sunshine.utils.NetworkUtils;
import com.oceanscan.sunshine.utils.OpenWeatherJsonUtils;
import com.oceanscan.sunshine.utils.PermissionUtil;
import com.oceanscan.sunshine.utils.SunshineWeatherUtils;

import java.net.URL;

import static com.oceanscan.sunshine.data.WeatherContract.WeatherEntry.CONTENT_URI;
import static com.oceanscan.sunshine.data.WeatherContract.WeatherEntry.buildWeatherUriWithDate;
import static com.oceanscan.sunshine.data.WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_SYNC_FLEXTIME_SECONDS;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_SYNC_INTERVAL_SECONDS;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_SYNC_TAG;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_UPDATE_MIN_DISTANCE;
import static com.oceanscan.sunshine.utils.Constants.LOCATION_UPDATE_MIN_TIME;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DATE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MAX_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MIN_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WEATHER_ID;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ForecastAdapter.ForecastAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_FORECAST_PROJECTION = {
            COLUMN_DATE,
            COLUMN_MAX_TEMP,
            COLUMN_MIN_TEMP,
            COLUMN_WEATHER_ID,
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;


    /*
     * This ID will be used to identify the Loader responsible for loading our weather forecast. In
     * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
     * We will still use this ID to initialize the loader and create the loader for best practice.
     * Please note that 44 was chosen arbitrarily. You can use whatever number you like, so long as
     * it is unique and consistent.
     */
    private static final int ID_FORECAST_LOADER = 44;
    private ForecastAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mForecastAdapter);
        showLoading();


        Log.i(TAG, "Length " + SunshinePreferences.getMyLocationCoordinates(MainActivity.this).length);


        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PermissionCode.ACCESS_FINE_LOCATION);

            } else {
                scheduleLocationService(MainActivity.this);
                getCurrentLocation();

            }
        } else {
            scheduleLocationService(MainActivity.this);
            getCurrentLocation();

        }

    }


    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PermissionCode.ACCESS_FINE_LOCATION);
//
//            } else {
//                if (SunshinePreferences.getMyLocationCoordinates(MainActivity.this).length == 0) {
//
//                    getCurrentLocation();
//                }
//
//            }
//        } else {
//            if (SunshinePreferences.getMyLocationCoordinates(MainActivity.this).length == 0) {
//
//                getCurrentLocation();
//            }
//
//        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case ID_FORECAST_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                String selection = getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     * <p>
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) showWeatherDataView();


    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }

    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
//        if (id == R.id.action_map) {
//            openPreferredLocationInMap();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    static void scheduleLocationService(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job locationJob = dispatcher.newJobBuilder()
                .setService(LocationService.class)
                .setTag(LOCATION_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        LOCATION_SYNC_INTERVAL_SECONDS,
                        LOCATION_SYNC_INTERVAL_SECONDS + LOCATION_SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(locationJob);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Constants.PermissionCode.ACCESS_FINE_LOCATION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                scheduleLocationService(MainActivity.this);
                Log.i(TAG, "Permission fine location allowed");
            } else {

                Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_LONG).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
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

    public void getCurrentLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Toast.makeText(MainActivity.this, "GPS and Network not enabled", Toast.LENGTH_LONG).show();
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

            SunshinePreferences.setMyLocationDetails(MainActivity.this, location.getLatitude(),
                    location.getLongitude());

            Log.i(TAG, String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()));

            getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
            SunshineSyncUtils.initialize(this);

        } else {
            Toast.makeText(getApplicationContext(),
                    "This application requires your location to get weather data",
                    Toast.LENGTH_LONG).show();
        }
    }
}
