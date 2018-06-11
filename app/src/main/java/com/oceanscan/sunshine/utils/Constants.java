package com.oceanscan.sunshine.utils;

import android.net.Uri;

import java.util.concurrent.TimeUnit;

import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MAX_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MIN_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WEATHER_ID;

public class Constants {


    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE_DAY = 1;

    public static class WeatherJSONUtils {
        public static final String OWM_CITY = "city";
        public static final String OWM_COORD = "coord";
        public static final String OWM_LATITUDE = "lat";
        public static final String OWM_LONGITUDE = "lon";
        public static final String OWM_LIST = "list";

        public static final String OWM_PRESSURE = "pressure";
        public static final String OWM_HUMIDITY = "humidity";
        public static final String OWM_WINDSPEED = "speed";
        public static final String OWM_WIND_DIRECTION = "deg";

        public static final String OWM_TEMPERATURE = "temp";
        public static final String OWM_MAX = "max";
        public static final String OWM_MIN = "min";

        public static final String OWM_WEATHER = "weather";
        public static final String OWM_WEATHER_ID = "id";

        public static final String OWM_MESSAGE_CODE = "cod";
    }

    public static class DateUtils {
        public static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    }

    public static class Notifications {
        public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
                COLUMN_WEATHER_ID,
                COLUMN_MAX_TEMP,
                COLUMN_MIN_TEMP,
        };
        public static final int INDEX_WEATHER_ID = 0;
        public static final int INDEX_MAX_TEMP = 1;
        public static final int INDEX_MIN_TEMP = 2;
    }

    public static class Network {
        public static final String DYNAMIC_WEATHER_URL =
                "https://andfun-weather.udacity.com/weather";

        public static final String STATIC_WEATHER_URL =
                "https://andfun-weather.udacity.com/staticweather";

        public static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;
        public static final String format = "json";
        public static final String units = "metric";
        public static final int numDays = 14;
        public static final String QUERY_PARAM = "q";

        public static final String LAT_PARAM = "lat";
        public static final String LON_PARAM = "lon";
        public static final String FORMAT_PARAM = "mode";
        public static final String UNITS_PARAM = "units";
        public static final String DAYS_PARAM = "cnt";

    }

    public static class Weather {
        public static final int CODE_WEATHER = 100;
        public static final int CODE_WEATHER_WITH_DATE = 101;
    }

    public static class Database {
        public static final String DATABASE_NAME = "weather.db";
        public static final int DATABASE_VERSION = 3;


    }

    public static class WeatherContract {
        public static final String CONTENT_AUTHORITY = "com.example.android.sunshine";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_WEATHER = "weather";
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";
    }

    public static class Preferences {
        public static final String PREF_COORD_LAT = "coord_lat";
        public static final String PREF_COORD_LONG = "coord_long";
    }

    public static final int VIEW_WEATHER_INTENT = 102;
    public static final int IGNORE_PENDING_INTENT = 101;
    public static final int PENDING_INTENT = 100;
    public static final String NOTIFICATION_CHANNEL = "Sunshine";
    public static final int SUNSHINE_NOTIFICATION_ID = 200;
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss_notification";
    public static final String ACTION_VIEW_WEATHER = "view_weather";

    public static class WeatherUtils {
        public static final int LOAD_INTERVAL_MINUTES = 1;
        public static final int LOAD_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(LOAD_INTERVAL_MINUTES);
        public static final int SYNC_FLEX_TIME_SECONDS = LOAD_INTERVAL_SECONDS;
        public static final String LOAD_JOB_TAG = "Load-Job-Tag";
        public static final String SUNSHINE_SYNC_TAG = "sunshine-sync";
        public static final int SYNC_INTERVAL_HOURS = 3;
        public static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
        public static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;


    }
}
