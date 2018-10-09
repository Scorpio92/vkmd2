package ru.scorpio92.vkmd2.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.old.view.activity.AuthActivity;
import ru.scorpio92.vkmd2.tools.JsonWorker;
import ru.scorpio92.vkmd2.tools.Logger;

public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.log("onMessageReceived: " + JsonWorker.getSerializeJson(remoteMessage.getNotification()));
        showNotification(remoteMessage);
    }

    private static final int NOTIFICATION_ID = 6663;
    private static final String channelName = "Channel push VKMD2";
    private NotificationManager notificationManager;
    private NotificationChannel mChannel;

    private void showNotification(RemoteMessage remoteMessage) {
        try {
            Intent notificationIntent = new Intent(this, AuthActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            final String channelId = getString(R.string.default_notification_channel_id);

            if (notificationManager == null) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (mChannel == null) {
                    mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                    mChannel.enableVibration(false);
                    mChannel.setVibrationPattern(null);
                    mChannel.enableLights(false);
                    mChannel.setSound(null, null);
                    notificationManager.createNotificationChannel(mChannel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.mipmap.ic_music_note_black_24dp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            }
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            builder.setOnlyAlertOnce(true);
            builder.setAutoCancel(true);
            builder.setSound(null);
            builder.setVibrate(null);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
