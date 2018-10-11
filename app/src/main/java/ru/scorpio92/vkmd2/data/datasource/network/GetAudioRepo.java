package ru.scorpio92.vkmd2.data.datasource.network;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.Request;
import retrofit2.Retrofit;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.datasource.network.base.API;
import ru.scorpio92.vkmd2.data.datasource.network.base.IGetAudioRepo;
import ru.scorpio92.vkmd2.data.datasource.network.core.RequestInterceptor;
import ru.scorpio92.vkmd2.data.datasource.network.core.RetrofitNetworkRepository;

import static ru.scorpio92.vkmd2.BuildConfig.BASE_URL;

public class GetAudioRepo extends RetrofitNetworkRepository<API> implements IGetAudioRepo {

    private String cookie;

    public GetAudioRepo(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public Observable<List<Track>> getAccountAudio(int offset) {
        /*return getApiInterface().getAccountAudio(offset)
                .flatMap(s -> Observable.just(VkmdUtils.getTrackListFromPageCode(s)));*/
        return null;
    }

    @Override
    public Observable<List<OnlineTrack>> getSearchAudio(String uid, String query) {
        /*return getApiInterface().getSearchAudio("search", query)
                .flatMap(s -> Observable.just(VkmdUtils.getTrackListFromSearchPageCode(uid, s)));*/
        return null;
    }

    @Override
    protected String provideBaseURL() {
        return BASE_URL;
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
}
