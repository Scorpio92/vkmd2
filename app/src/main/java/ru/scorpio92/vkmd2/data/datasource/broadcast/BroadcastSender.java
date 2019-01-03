package ru.scorpio92.vkmd2.data.datasource.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

import ru.scorpio92.vkmd2.domain.datasource.IBroadcastSender;
import ru.scorpio92.vkmd2.domain.entity.BroadcastData;

public class BroadcastSender implements IBroadcastSender {

    private WeakReference<Context> contextWeakReference;
    private String action;

    public BroadcastSender(Context context, String action) {
        this.contextWeakReference = new WeakReference<>(context);
        this.action = action;
    }

    @Override
    public void sendBroadcastEvent(String event) {
        sendBroadcastMessage(new BroadcastData(event, null, null));
    }

    @Override
    public void sendBroadcastMessage(BroadcastData broadcastData) {
        if (broadcastData != null) {
            Intent intent = new Intent(action);
            intent.putExtra(BroadcastData.BROADCAST_EVENT_PARAM_NAME, broadcastData.getEvent());
            intent.putExtra(BroadcastData.BROADCAST_DATA_PARAM_NAME, broadcastData.getSerializedData());
            if (broadcastData.getDataType() != null) {
                intent.putExtra(BroadcastData.BROADCAST_DATA_CLASS_PARAM_NAME, broadcastData.getDataType().getSimpleName());
            }
            if (contextWeakReference != null && contextWeakReference.get() != null) {
                LocalBroadcastManager.getInstance(contextWeakReference.get()).sendBroadcast(intent);
            }
        }
    }
}
