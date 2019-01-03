package ru.scorpio92.vkmd2.data.datasource.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;

import ru.scorpio92.vkmd2.domain.datasource.IBroadcastReceiver;
import ru.scorpio92.vkmd2.domain.entity.BroadcastData;

public class BroadcastReceiver implements IBroadcastReceiver {

    private WeakReference<Context> contextWeakReference;
    private String action;
    private Listener listener;

    public BroadcastReceiver(Context context, String action) {
        this.contextWeakReference = new WeakReference<>(context);
        this.action = action;
    }


    @Override
    public void initialize(Listener listener) {
        if (contextWeakReference != null && contextWeakReference.get() != null) {
            this.listener = listener;
            LocalBroadcastManager.getInstance(contextWeakReference.get()).registerReceiver(
                    broadcastReceiver, new IntentFilter(action));
        }
    }

    @Override
    public void finish() {
        listener = null;
        if (contextWeakReference != null) {
            LocalBroadcastManager.getInstance(contextWeakReference.get()).unregisterReceiver(broadcastReceiver);
            contextWeakReference.clear();
            contextWeakReference = null;
        }
    }

    private android.content.BroadcastReceiver broadcastReceiver = new android.content.BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String event = bundle.getString(BroadcastData.BROADCAST_EVENT_PARAM_NAME);
                if (event != null && listener != null) {
                    try {
                        String data = bundle.getString(BroadcastData.BROADCAST_DATA_PARAM_NAME);
                        String dataClass = bundle.getString(BroadcastData.BROADCAST_DATA_CLASS_PARAM_NAME);
                        Class c = null;
                        if (dataClass != null) {
                            c = Class.forName(dataClass);
                        }
                        listener.onResult(new BroadcastData(event, data, c));
                    } catch (Exception e) {
                        listener.onError(e);
                    }
                }
            }
        }
    };
}
