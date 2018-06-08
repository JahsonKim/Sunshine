package com.oceanscan.sunshine.utils;

import java.util.concurrent.TimeUnit;

public class Constants {
    public static int WEATHER_LOADER = 22;
    public final static String GITHUB_BASE_URL = "https://api.github.com/search/repositories";
    public static final String SEARCH_QUERY_URL_EXTRA = "query";

    public static String LOCATION="Location";

    public static class Preferences{
        /*
         * Human readable location string, provided by the API.  Because for styling,
         * "Mountain View" is more recognizable than 94043.
         */
        public static final String PREF_CITY_NAME = "city_name";

        /*
         * In order to uniquely pinpoint the location on the map when we launch the
         * map intent, we store the latitude and longitude.
         */
        public static final String PREF_COORD_LAT = "coord_lat";
        public static final String PREF_COORD_LONG = "coord_long";

        /*
         * Before you implement methods to return your REAL preference for location,
         * we provide some default values to work with.
         */
        public static final String DEFAULT_WEATHER_LOCATION = "94043,USA";
        public static final double[] DEFAULT_WEATHER_COORDINATES = {37.4284, 122.0724};

        public static final String DEFAULT_MAP_LOCATION =
                "1600 Amphitheatre Parkway, Mountain View, CA 94043";

    }

    public static final int VIEW_WEATHER_INTENT = 102;
    public static final int IGNORE_PENDING_INTENT = 101;
    public static final int PENDING_INTENT = 100;
    public static final String NOTIFICATION_CHANNEL = "Sunshine";
    public static final int SUNSHINE_NOTIFICATION_ID = 200;
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss_notification";
    public static final String ACTION_VIEW_WEATHER = "view_weather";

    public static class WeatherUtils{
        public static final int LOAD_INTERVAL_MINUTES = 1;
        public static final int LOAD_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(LOAD_INTERVAL_MINUTES);
        public static final int SYNC_FLEX_TIME_SECONDS = LOAD_INTERVAL_SECONDS;
        public static final String LOAD_JOB_TAG = "Load-Job-Tag";

    }
}
