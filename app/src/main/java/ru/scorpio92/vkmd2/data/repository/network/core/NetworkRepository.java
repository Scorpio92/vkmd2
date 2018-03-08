package ru.scorpio92.vkmd2.data.repository.network.core;

import android.util.Pair;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.scorpio92.vkmd2.tools.Logger;


public abstract class NetworkRepository implements INetworkRepository {

    private static final String LOG_TAG = NetworkRepository.class.getSimpleName();

    private volatile HttpURLConnection connection;
    private InputStream inputStream;
    private ByteArrayOutputStream byteArrayOutputStream;

    protected final void makeRequest(RequestSpecification requestSpecification, NetworkCallback callback) {
        try {
            Logger.log(LOG_TAG, "request: " + requestSpecification.getUrl());
            URL url = new URL(requestSpecification.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            for (Pair<String, String> header : requestSpecification.getHeaders())
                connection.setRequestProperty(header.first, header.second);
            connection.setConnectTimeout(requestSpecification.getConnectionTimeout());
            connection.setDoOutput(false);
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                int lengthOfFile = connection.getContentLength();
                byteArrayOutputStream = new ByteArrayOutputStream();
                byte data[] = new byte[1024];
                int count;
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    if (callback != null && callback instanceof PercentNetworkCallback)
                        ((PercentNetworkCallback) callback).onProgressUpdate((int) ((total * 100) / lengthOfFile));

                    byteArrayOutputStream.write(data, 0, count);
                }
                if (callback != null) {
                    if (callback instanceof PercentNetworkCallback)
                        ((PercentNetworkCallback) callback).onProgressUpdate(100);

                    byte[] result = byteArrayOutputStream.toByteArray();
                    Logger.log(LOG_TAG, "response: " + new String(result, "UTF-8"));
                    callback.onComplete(result);
                }
            } else {
                if (callback != null)
                    callback.onError(new BadResponseCodeException(responseCode));
            }

        } catch (Exception e) {
            Logger.error(e);
            if (callback != null)
                callback.onError(new BadRequestException());
        } finally {
            interrupt();
        }
    }

    private void interrupt() {
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    @Override
    public void cancel() {
        new Thread(this::interrupt).start();
    }
}
