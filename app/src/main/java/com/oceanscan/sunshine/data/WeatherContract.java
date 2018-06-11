package com.oceanscan.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.oceanscan.sunshine.utils.SunshineDateUtils;

import static com.oceanscan.sunshine.utils.Constants.WeatherContract.BASE_CONTENT_URI;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DATE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.PATH_WEATHER;

public class WeatherContract {

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.example.android.sunshine/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.example.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */


    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        /* Used internally as the name of our weather table. */

        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load weather data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */


        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param date Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}
