package ru.scorpio92.vkmd2.data.repository.network;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import ru.scorpio92.vkmd2.data.repository.network.base.API;
import ru.scorpio92.vkmd2.data.repository.network.base.IDownloadtAudioRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.RetrofitNetworkRepository;
import ru.scorpio92.vkmd2.tools.Logger;

public class DownloadAudioRepo extends RetrofitNetworkRepository<API> implements IDownloadtAudioRepo {

    private String url;
    private String savePath;

    public DownloadAudioRepo(String url, String savePath) {
        this.url = url;
        this.savePath = savePath;
    }

    @Override
    public Observable<Integer> downloadTrack() {
        return Observable.create(emitter -> {
            try {
                ResponseBody body = getApiInterface().downloadTrack(url).execute().body();

                InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
                File outputFile = new File(savePath);
                OutputStream output = new FileOutputStream(outputFile);

                long lengthOfFile = body.contentLength();
                byte data[] = new byte[1024 * 4];
                int count;
                long total = 0;

                while ((count = bis.read(data)) != -1) {
                    total += count;
                    if (!emitter.isDisposed()) {
                        output.write(data, 0, count);
                        emitter.onNext((int) ((total * 100) / lengthOfFile));
                    }
                }

                try {
                    output.flush();
                    output.close();
                    bis.close();
                } catch (Exception e) {
                    Logger.error(e);
                }

                if (!emitter.isDisposed())
                    emitter.onComplete();

            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    @Override
    protected API createApiInterface(Retrofit client) {
        return client.create(API.class);
    }

    @Override
    protected boolean enableLogging() {
        return false;  //выключаем логирование, т.к. оно держит InputStream
    }
}
