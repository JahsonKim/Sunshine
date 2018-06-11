package com.oceanscan.sunshine.utils;

import android.content.ContentValues;
import android.content.Context;

import com.oceanscan.sunshine.data.SunshinePreferences;
import com.oceanscan.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import static com.oceanscan.sunshine.utils.Constants.DateUtils.DAY_IN_MILLIS;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DATE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DEGREES;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_HUMIDITY;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MAX_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MIN_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_PRESSURE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WEATHER_ID;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WIND_SPEED;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_CITY;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_COORD;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_HUMIDITY;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_LATITUDE;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_LIST;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_LONGITUDE;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_MAX;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_MESSAGE_CODE;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_MIN;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_PRESSURE;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_TEMPERATURE;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_WEATHER;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_WEATHER_ID;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_WINDSPEED;
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.OWM_WIND_DIRECTION;

public class OpenWeatherJsonUtils {


    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

        SunshinePreferences.setLocationDetails(context, cityLatitude, cityLongitude);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);

        long normalizedUtcStartDay = SunshineDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {

            long dateTimeMillis;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            int weatherId;

            /* Get the JSON object representing the day */
            JSONObject dayForecast = jsonWeatherArray.getJSONObject(i);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            dateTimeMillis = normalizedUtcStartDay + DAY_IN_MILLIS * i;

            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary variable, temporary folder, temporary employee, or many
             * others, and is just a bad variable name.
             */
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(COLUMN_DATE, dateTimeMillis);
            weatherValues.put(COLUMN_HUMIDITY, humidity);
            weatherValues.put(COLUMN_PRESSURE, pressure);
            weatherValues.put(COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(COLUMN_DEGREES, windDirection);
            weatherValues.put(COLUMN_MAX_TEMP, high);
            weatherValues.put(COLUMN_MIN_TEMP, low);
            weatherValues.put(COLUMN_WEATHER_ID, weatherId);

            weatherContentValues[i] = weatherValues;
        }

        return weatherContentValues;
    }
}
