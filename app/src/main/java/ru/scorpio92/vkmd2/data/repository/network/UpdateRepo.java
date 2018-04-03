package ru.scorpio92.vkmd2.data.repository.network;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import ru.scorpio92.vkmd2.App;
import ru.scorpio92.vkmd2.BuildConfig;
import ru.scorpio92.vkmd2.data.repository.network.base.API;
import ru.scorpio92.vkmd2.data.repository.network.base.IUpdateRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.RetrofitNetworkRepository;
import ru.scorpio92.vkmd2.tools.Logger;

public class UpdateRepo extends RetrofitNetworkRepository<API> implements IUpdateRepo {

    @Override
    public Observable<String> getLastVersion() {
        return Observable.fromCallable(() -> {
            ResponseBody body = getApiInterface().getLastVersion(BuildConfig.GITHUB_REPO_RAW_URL + BuildConfig.GITHUB_REPO_BUILD_CONFIG_URL).execute().body();

            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = bis.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String config = result.toString("UTF-8");
            try {
                result.flush();
                result.close();
            } catch (IOException ioe) {
                Logger.error(ioe);
            }

            Pattern p = Pattern.compile("versionName \"(.*?)\"");
            Matcher m = p.matcher(config);
            if (m.find())
                return m.group(1);
            else
                throw new Exception("can not found app version");
        });
    }

    @Override
    public Observable<String> downloadLastBuild(String version) {
        return Observable.fromCallable(() -> {
            ResponseBody body = getApiInterface().downloadLastBuild(BuildConfig.GITHUB_REPO_RAW_URL
                    + BuildConfig.GITHUB_REPO_RELEASE_FOLDER + "/"
                    + BuildConfig.BUILD_MAIN_APP_NAME + "-" + version)
                    .execute().body();

            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
            String path = App.APP_DIR + "/"
                    + BuildConfig.UPDATE_FOLDER + "/"
                    + BuildConfig.BUILD_MAIN_APP_NAME + "-" + version;
            File outputFile = new File(path);
            OutputStream output = new FileOutputStream(outputFile);

            byte data[] = new byte[1024 * 4];
            int count;

            while ((count = bis.read(data)) != -1) {
                output.write(data, 0, count);
            }

            try {
                output.flush();
                output.close();
                bis.close();
            } catch (Exception e) {
                Logger.error(e);
            }

            return path;
        });
    }

    @Override
    protected API createApiInterface(Retrofit client) {
        return client.create(API.class);
    }
}
