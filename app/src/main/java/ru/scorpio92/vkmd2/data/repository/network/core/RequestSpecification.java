package ru.scorpio92.vkmd2.data.repository.network.core;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


public abstract class RequestSpecification {

    private String url;
    private List<Pair<String, String>> headers = new ArrayList<>();
    private int connectionTimeout = 30000;

    public RequestSpecification(String url) {
        this.url = url;
    }

    public void setHeaders(List<Pair<String, String>> headers) {
        this.headers = headers;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    public String getUrl() {
        return url;
    }

    public List<Pair<String, String>> getHeaders() {
        return headers;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }
}
