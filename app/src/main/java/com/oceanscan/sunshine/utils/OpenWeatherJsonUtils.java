package com.oceanscan.sunshine.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

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
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.MAIN;
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
import static com.oceanscan.sunshine.utils.Constants.WeatherJSONUtils.WIND;

public class OpenWeatherJsonUtils {
private static final String TAG=OpenWeatherJsonUtils.class.getSimpleName();

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

       // Log.i(TAG,forecastJsonStr);
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

//
//        {"coord":
//            {"lon":36.97,"lat":-1.13}
//            ,"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}]
//            ,"base":"stations",
//                "main":{"temp":295.64,"pressure":1024,"humidity":56,"temp_min":295.15,"temp_max":296.15},
//            "visibility":10000,"wind":{"speed":4.1,"deg":100},
//            "clouds":{"all":75},
//            "dt":1528804800,
//                "sys":{"type":1,
//                "id":6409,"message":0.0027,"country":"KE","sunrise":1528774219,"sunset":1528817628},
//            "id":179330,"name":"Thika","cod":200}

//        "list":[{"dt":1528815600,
//                "main":
//            {"temp":291.91,"temp_min":290.516,"temp_max":291.91,
//                    "pressure":866.51,"sea_level":1027.87,"grnd_level":866.51,
//                    "humidity":94,"temp_kf":1.39},
//            "weather":
//            [{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],
//            "clouds":{"all":76},
//            "wind":{"speed":1.71,
//                    "deg":121.002},
//            "rain":{"3h":1.005},
//            "sys":{"pod":"d"},
//            "dt_txt":"2018-06-12 15:00:00"}],
//        "city":{"id":179330,"name":"Thika",
//                "coord":{"lat":-1.0333,"lon":37.0693},
//            "country":"KE","population":99322}


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
//            [{"dt":1528815600,
//                    "main":
//                {"temp":291.91,"temp_min":290.516,"temp_max":291.91,
//                        "pressure":866.51,"sea_level":1027.87,"grnd_level":866.51,
//                        "humidity":94,"temp_kf":1.39},
//                "weather":
//            [{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],
//                "clouds":{"all":76},
//                "wind":{"speed":1.71,
//                        "deg":121.002},
//                "rain":{"3h":1.005},
//                "sys":{"pod":"d"},
//                "dt_txt":"2018-06-12 15:00:00"}]

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
 dateTimeMillis = normalizedUtcStartDay + DAY_IN_MILLIS * i;

            //get main weather details
            JSONObject mainJSONObject = dayForecast.getJSONObject(MAIN);
//            "main":
//            {"temp":291.91,"temp_min":290.516,"temp_max":291.91,
//                    "pressure":866.51,"sea_level":1027.87,"grnd_level":866.51,
//                    "humidity":94,"temp_kf":1.39},
 pressure = mainJSONObject.getDouble(OWM_PRESSURE);
            humidity = mainJSONObject.getInt(OWM_HUMIDITY);
           high = convertKelvinToCelsius(mainJSONObject.getDouble(OWM_MAX));
            low = convertKelvinToCelsius(mainJSONObject.getDouble(OWM_MIN));

           // Log.i(TAG,"max "+convertKelvinToCelsius(high));

//            "wind":{"speed":1.71,
//                    "deg":121.002},
            JSONObject windObject=dayForecast.getJSONObject(WIND);
            windSpeed = windObject.getDouble(OWM_WINDSPEED);
            windDirection = windObject.getDouble(OWM_WIND_DIRECTION);

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);



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

    public static double convertKelvinToCelsius(double kelvin){
        double celsius;
        celsius = kelvin - 273.0;
        return celsius;
    }
    public static double convertKelvinToFahrenheit(double kelvin) {
        double celsius, fahrenhiet;
        celsius = kelvin - 273.0;
        fahrenhiet = (celsius * 9.0/5.0) + 32.0;
        return fahrenhiet;
    }

}
