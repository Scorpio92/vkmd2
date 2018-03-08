package ru.scorpio92.vkmd2.data.repository.network.core;


public interface NetworkCallback {

    void onComplete(byte[] bytes);

    void onError(Exception e);
}
