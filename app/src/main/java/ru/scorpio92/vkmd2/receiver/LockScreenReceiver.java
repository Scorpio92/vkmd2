package ru.scorpio92.vkmd2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import ru.scorpio92.vkmd2.service.AudioService;


public class LockScreenReceiver extends BroadcastReceiver {

    final String LOG_TAG = "LockScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            final KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    context.startService(new Intent(context, AudioService.class)
                            .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PLAY_OR_PAUSE.name())
                    );
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    context.startService(new Intent(context, AudioService.class)
                            .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.NEXT.name())
                    );
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    context.startService(new Intent(context, AudioService.class)
                            .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PREV.name())
                    );
                }
            }
        }
    }
}
