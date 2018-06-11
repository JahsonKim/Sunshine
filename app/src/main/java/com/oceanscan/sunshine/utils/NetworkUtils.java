package com.oceanscan.sunshine.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.oceanscan.sunshine.data.SunshinePreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.oceanscan.sunshine.utils.Constants.Network.DAYS_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.FORECAST_BASE_URL;
import static com.oceanscan.sunshine.utils.Constants.Network.FORMAT_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.LAT_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.LON_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.QUERY_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.UNITS_PARAM;
import static com.oceanscan.sunshine.utils.Constants.Network.format;
import static com.oceanscan.sunshine.utils.Constants.Network.numDays;
import static com.oceanscan.sunshine.utils.Constants.Network.units;


public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

        /*
         * Sunshine was originally built to use OpenWeatherMap's API. However, we wanted to provide
         * a way to much more easily test the app and provide more varied weather data. After all, in
         * Mountain View (Google's HQ), it gets very boring looking at a forecast of perfectly clear
         * skies at 75°F every day... (UGH!) The solution we came up with was to host our own fake
         * weather server. With this server, there are two URL's you can use. The first (and default)
         * URL will return dynamic weather data. Each time the app refreshes, you will get different,
         * completely random weather data. This is incredibly useful for testing the robustness of your
         * application, as different weather JSON will provide edge cases for some of your methods.
         *
         * If you'd prefer to test with the weather data that you will see in the videos on Udacity,
         * you can do so by setting the FORECAST_BASE_URL to STATIC_WEATHER_URL below.
         */


        /**
         * Retrieves the proper URL to query for the weather data. The reason for both this method as
         * well as {@link #buildUrlWithLocationQuery(String)} is two fold.
         * <p>
         * 1) You should be able to just use one method when you need to create the URL within the
         * app instead of calling both methods.
         * 2) Later in Sunshine, you are going to add an alternate method of allowing the user
         * to select their preferred location. Once you do so, there will be another way to form
         * the URL using a latitude and longitude rather than just a location String. This method
         * will "decide" which URL to build and return it.
         *
         * @param context used to access other Utility methods
         * @return URL to query weather service
         */
        public static URL getUrl(Context context) {
            if (SunshinePreferences.isLocationLatLonAvailable(context)) {
                double[] preferredCoordinates = SunshinePreferences.getLocationCoordinates(context);
                double latitude = preferredCoordinates[0];
                double longitude = preferredCoordinates[1];
                return buildUrlWithLatitudeLongitude(latitude, longitude);
            } else {
                String locationQuery = SunshinePreferences.getPreferredWeatherLocation(context);
                return buildUrlWithLocationQuery(locationQuery);
            }
        }

        /**
         * Builds the URL used to talk to the weather server using latitude and longitude of a
         * location.
         *
         * @param latitude  The latitude of the location
         * @param longitude The longitude of the location
         * @return The Url to use to query the weather server.
         */
        private static URL buildUrlWithLatitudeLongitude(Double latitude, Double longitude) {
            Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(LAT_PARAM, String.valueOf(latitude))
                    .appendQueryParameter(LON_PARAM, String.valueOf(longitude))
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();

            try {
                URL weatherQueryUrl = new URL(weatherQueryUri.toString());
                Log.v(TAG, "URL: " + weatherQueryUrl);
                return weatherQueryUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Builds the URL used to talk to the weather server using a location. This location is based
         * on the query capabilities of the weather provider that we are using.
         *
         * @param locationQuery The location that will be queried for.
         * @return The URL to use to query the weather server.
         */
        private static URL buildUrlWithLocationQuery(String locationQuery) {
            Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, locationQuery)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();

            try {
                URL weatherQueryUrl = new URL(weatherQueryUri.toString());
                Log.v(TAG, "URL: " + weatherQueryUrl);
                return weatherQueryUrl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * This method returns the entire result from the HTTP response.
         *
         * @param url The URL to fetch the HTTP response from.
         * @return The contents of the HTTP response, null if no response
         * @throws IOException Related to network and stream reading
         */
        public static String getResponseFromHttpUrl(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                String response = null;
                if (hasInput) {
                    response = scanner.next();
                }
                scanner.close();
                return response;
            } finally {
                urlConnection.disconnect();
            }
        }
}