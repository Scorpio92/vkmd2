package ru.scorpio92.vkmd2.data.repository.network;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import okhttp3.Request;
import retrofit2.Retrofit;
import ru.scorpio92.vkmd2.data.repository.network.base.API;
import ru.scorpio92.vkmd2.data.repository.network.core.RequestInterceptor;
import ru.scorpio92.vkmd2.data.repository.network.core.RetrofitNetworkRepository;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.tools.VkmdUtils;

import static ru.scorpio92.vkmd2.BuildConfig.BASE_URL;

public class VkAudioDataSource extends RetrofitNetworkRepository<API> implements ITrackDataSource.Remote {

    private String cookie;

    public VkAudioDataSource(@NonNull String cookie) {
        this.cookie = cookie;
    }

    @Override
    public Single<List<Track>> getAccountAudio(int offset) {
        return getApiInterface().getAccountAudio(offset)
                .flatMap(s -> Single.just(VkmdUtils.getTrackListFromPageCode(s)));
    }

    @Override
    public Single<List<Track>> searchAudio(String uid, String query) {
        return getApiInterface().getSearchAudio("search", query)
                .flatMap(s -> Single.just(VkmdUtils.getTrackListFromSearchPageCode(uid, s)));
    }

    @Override
    protected API createApiInterface(Retrofit client) {
        return client.create(API.class);
    }

    @Override
    protected RequestInterceptor provideRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            protected void onRequest(Request.Builder builder) {
                builder.addHeader("cookie", cookie);
            }
        };
    }

    @Override
    protected String provideBaseURL() {
        return BASE_URL;
    }
}
