package ru.scorpio92.vkmd2.data.repository.network.core;


public class BadResponseCodeException extends Exception {

    private int responseCode;

    public BadResponseCodeException(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
