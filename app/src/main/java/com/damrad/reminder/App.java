package com.damrad.reminder;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {

    public static final String CHANNEL_1_ID = "com.damrad.reminder.CHANNEL_ONE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_1_ID,
                getString(R.string.general),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
