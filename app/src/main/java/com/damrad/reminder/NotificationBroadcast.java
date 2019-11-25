package com.damrad.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.damrad.reminder.App.CHANNEL_1_ID;

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String titleExtra = intent.getStringExtra("titleExtra");
        String bodyExtra = intent.getStringExtra("bodyExtra");

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setContentTitle(titleExtra)
                .setContentText(bodyExtra)
                .setSmallIcon(R.drawable.ic_event_note_white)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setCategory(Notification.CATEGORY_ALARM)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, notification);

    }
}
