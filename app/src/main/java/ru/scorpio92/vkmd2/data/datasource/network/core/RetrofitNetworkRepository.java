package ru.scorpio92.vkmd2.data.datasource.network.core;


import android.annotation.SuppressLint;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import ru.scorpio92.vkmd2.BuildConfig;

public abstract class RetrofitNetworkRepository<I> implements IRetrofitNetworkRepository {

    protected String provideBaseURL() {
        return "http://127.0.0.1/";
    }

    protected abstract I createApiInterface(Retrofit client);

    protected I getApiInterface() {
        return createApiInterface(init(provideBaseURL()));
    }

    protected RequestInterceptor provideRequestInterceptor() {
        return null;
    }

    protected boolean enableLogging() {
        return BuildConfig.DEBUG;
    }

    private Retrofit init(String baseURL) {
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(getUnsafeOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();


    }

    private OkHttpClient getUnsafeOkHttpClient() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.hostnameVerifier(new DummyHostnameVerifier());
        try {
            builder.sslSocketFactory(new TLSSocketFactory(), (X509TrustManager) trustAllCerts[0]);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //добавляем логирование запросов/ответов
        if(enableLogging()) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        RequestInterceptor requestInterceptor = provideRequestInterceptor();
        if (requestInterceptor != null)
            builder.addInterceptor(requestInterceptor);

        return builder.build();
    }

    @Override
    public void cancel() {

    }
}
