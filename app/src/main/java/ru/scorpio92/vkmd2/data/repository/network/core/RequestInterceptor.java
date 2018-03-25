package ru.scorpio92.vkmd2.data.repository.network.core;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        onRequest(builder);
        return chain.proceed(builder.build());
    }

    protected abstract void onRequest(Request.Builder builder);
}
