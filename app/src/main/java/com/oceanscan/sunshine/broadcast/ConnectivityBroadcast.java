package com.oceanscan.sunshine.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.oceanscan.sunshine.MainActivity;

public class ConnectivityBroadcast extends BroadcastReceiver {
    public static String TAG = ConnectivityBroadcast.class.getSimpleName();


    public static ConnectivityReceiverListener connectivityReceiverListener;

    Context context;


    public ConnectivityBroadcast() {
        super();

    }

    public boolean isOnline(Context context)
    {
        boolean isOnline = false;
        try
        {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            isOnline = (netInfo != null && netInfo.isConnected());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return isOnline;
    }

    public boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        this.context = context;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
        if (isConnected) {
         //   MainActivity.showConnection(isConnected);
            Log.i(TAG, "connected to the internet");
        } else {
          //  MainActivity.showConnection(false);
            Log.i(TAG, "Not connected to the internet");
            //  Toast.makeText(context, "isNotConnected", Toast.LENGTH_LONG).show();
        }
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }



    //Socket listener


}

