package com.oceanscan.sunshine.service;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class WeatherService extends JobService {
    AsyncTask mBackgroundTask;
    private static String TAG=WeatherService.class.getSimpleName();
    @Override
    public boolean onStartJob(final JobParameters params) {
        mBackgroundTask=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.i(TAG,"start task here");
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                jobFinished(params,false);
                //false no need to reschedule
               // NotificationUtils1.notifyUserWeather(WeatherService.this);

            }
        };
        mBackgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //stop job for instance if job was started when wifi was on and then put off
        if(mBackgroundTask!=null){
            mBackgroundTask.cancel(true);
        }
        return true;
    }
}
