package com.oceanscan.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.oceanscan.sunshine.utils.Constants.Database.DATABASE_NAME;
import static com.oceanscan.sunshine.utils.Constants.Database.DATABASE_VERSION;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DATE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_DEGREES;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_HUMIDITY;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MAX_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_MIN_TEMP;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_PRESSURE;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WEATHER_ID;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.COLUMN_WIND_SPEED;
import static com.oceanscan.sunshine.utils.Constants.WeatherContract.TABLE_NAME;

public class WeatherDbHelper extends SQLiteOpenHelper {





    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

         /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
  final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " INTEGER NOT NULL, " +
                        COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                        COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                        COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                        COLUMN_HUMIDITY + " REAL NOT NULL, " +
                        COLUMN_PRESSURE + " REAL NOT NULL, " +
                        COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                        COLUMN_DEGREES + " REAL NOT NULL, " +
                        " UNIQUE (" + COLUMN_DATE + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}