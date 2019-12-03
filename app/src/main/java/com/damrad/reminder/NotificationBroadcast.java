package com.damrad.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.damrad.reminder.App.CHANNEL_1_ID;

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getExtras()).size() >= 4) {

            String managerType = intent.getStringExtra("OperationType");
            String titleExtra = intent.getStringExtra("titleExtra");
            String bodyExtra = intent.getStringExtra("bodyExtra");
            int id = intent.getIntExtra("ID", 0);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            if (managerType.equals("ADD")) {
                Intent notificationIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                        .setContentTitle(titleExtra)
                        .setContentText(bodyExtra)
                        .setSmallIcon(R.drawable.ic_add_alert)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .build();

                notificationManager.notify(id, notification);

            } else if (managerType.equals("DELETE")) {
                Toast.makeText(context, "USUNIETO: " + id, Toast.LENGTH_LONG).show();
                notificationManager.cancel(id);
            }
        }
    }
}
