package com.bangbangcoding.screenmirror.web.ui.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.bangbangcoding.screenmirror.R;
import com.bangbangcoding.screenmirror.web.ui.studio.DownloadActivity;


public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "com.highsecure.videodownloader.ui.DOWNLOAD_CHANNEL";
    private static final String CHANNEL_NAME = "G_BROWSER Channel";
    private NotificationManager manager;
    private Intent intent;
    private long when;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannels() {
        intent = new Intent(this, DownloadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        when = System.currentTimeMillis();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getEDMTChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setWhen(when)
                .setSound(null)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
    }
}
