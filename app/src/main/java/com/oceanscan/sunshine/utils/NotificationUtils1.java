package com.oceanscan.sunshine.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.oceanscan.sunshine.MainActivity;
import com.oceanscan.sunshine.R;

public class NotificationUtils1 {

    public static PendingIntent contentInten(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                Constants.PENDING_INTENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.art_storm);
        return largeIcon;
    }

    public static void notifyUserWeather(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,
                Constants.NOTIFICATION_CHANNEL
        ).setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.art_clouds)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.app_name)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentInten(context))
                .addAction(ignoreWeatherReminder(context))
                .addAction(viewWeather(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }


        notificationManager.notify(Constants.SUNSHINE_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static NotificationCompat.Action ignoreWeatherReminder(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreIntent = PendingIntent.getActivity(
                context,
                Constants.IGNORE_PENDING_INTENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Action ignoreAction = new NotificationCompat.Action(R.drawable.art_clear, "No Thanks",
                ignoreIntent);
        return ignoreAction;
    }

    public static NotificationCompat.Action viewWeather(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.ACTION_VIEW_WEATHER);
        PendingIntent viewIntent = PendingIntent.getActivity(
                context,
                Constants.VIEW_WEATHER_INTENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Action viewAction = new NotificationCompat.Action(R.drawable.art_clouds,
                "View",
                viewIntent);
        return viewAction;
    }

}
