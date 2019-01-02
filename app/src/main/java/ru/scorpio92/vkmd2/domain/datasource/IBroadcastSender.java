package ru.scorpio92.vkmd2.domain.datasource;

import ru.scorpio92.vkmd2.domain.entity.BroadcastData;

public interface IBroadcastSender {

    void sendBroadcastEvent(String event);

    void sendBroadcastMessage(BroadcastData broadcastData);
}
