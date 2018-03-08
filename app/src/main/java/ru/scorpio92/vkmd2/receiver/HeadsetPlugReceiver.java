package ru.scorpio92.vkmd2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.scorpio92.vkmd2.service.AudioService;
import ru.scorpio92.vkmd2.tools.Logger;


public class HeadsetPlugReceiver extends BroadcastReceiver {

    final String LOG_TAG = "HeadsetPlugReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            return;
        }

        boolean connectedHeadphones = (intent.getIntExtra("state", 0) == 1);
        //boolean connectedMicrophone = (intent.getIntExtra("microphone", 0) == 1) && connectedHeadphones;

        Logger.log(LOG_TAG, "connectedHeadphones: " + Boolean.toString(connectedHeadphones));
        if (!connectedHeadphones) {
            context.startService(new Intent(context, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PAUSE.name())
            );
        }
    }
}
