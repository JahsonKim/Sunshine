package com.oceanscan.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SunshineSyncIntentService extends IntentService {
    private static String TAG = SunshineSyncIntentService.class.getSimpleName();

    public SunshineSyncIntentService() {

        super("SunshineSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Sync immediately");
        SunshineSyncTask.syncWeather(this);
    }
}