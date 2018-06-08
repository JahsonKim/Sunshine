package com.oceanscan.sunshine.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.oceanscan.sunshine.models.TaskDao;
import com.oceanscan.sunshine.models.TaskEntry;
import com.oceanscan.sunshine.utils.DateConverter;

@Database(entities = {TaskEntry.class}, exportSchema = false, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    static String TAG = AppDatabase.class.getSimpleName();
    static AppDatabase instance;
    static final Object LOCK = new Object();
    private static String DATABASE_NAME = "TodoList";

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                Log.i(TAG, "Createing database");
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        //allow main thread queries temporarily SHOULD never be done
                        .allowMainThreadQueries()
                        .build();

            }
        }
        Log.i(TAG, "Reading from database");

        return instance;
    }

    public abstract TaskDao taskDao();

}
