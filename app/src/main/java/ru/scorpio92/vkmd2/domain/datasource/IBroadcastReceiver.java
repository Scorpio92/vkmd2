package ru.scorpio92.vkmd2.domain.datasource;

import ru.scorpio92.vkmd2.domain.entity.BroadcastData;

public interface IBroadcastReceiver {

    void initialize(Listener listener);

    void finish();

    interface Listener {

        void onResult(BroadcastData broadcastData);

        void onError(Exception e);
    }
}
