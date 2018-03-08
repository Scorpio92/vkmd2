package ru.scorpio92.vkmd2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.scorpio92.vkmd2.service.DownloadService;
import ru.scorpio92.vkmd2.tools.Logger;


public class DownloadServiceNotificationReceiver extends BroadcastReceiver {

    final String LOG_TAG = "DownloadServiceNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            String action = intent.getAction();
            Logger.log(LOG_TAG, "action: " + action);
            if (action.equals(DownloadService.NOTIFICATION_ACTION_START_PAUSE)) {
                context.startService(new Intent(context, DownloadService.class)
                        .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.START_PAUSE.name())
                );
            }
            if (action.equals(DownloadService.NOTIFICATION_ACTION_STOP)) {
                context.startService(new Intent(context, DownloadService.class)
                        .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.STOP.name())
                );
            }
        }
    }
}
