package ru.scorpio92.vkmd2.data.repository.network;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.Request;
import retrofit2.Retrofit;
import ru.scorpio92.vkmd2.Constants;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.network.base.API;
import ru.scorpio92.vkmd2.data.repository.network.base.IGetAudioRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.RequestInterceptor;
import ru.scorpio92.vkmd2.data.repository.network.core.RetrofitNetworkRepository;
import ru.scorpio92.vkmd2.tools.VkmdUtils;

public class GetAudioRepo extends RetrofitNetworkRepository<API> implements IGetAudioRepo {

    private String cookie;

    public GetAudioRepo(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public Observable<List<Track>> getAccountAudio(int offset) {
        return getApiInterface().getAccountAudio(offset)
                .flatMap(s -> Observable.just(VkmdUtils.getTrackListFromPageCode(s)));
    }

    @Override
    public Observable<List<OnlineTrack>> getSearchAudio(String uid, String query) {
        return getApiInterface().getSearchAudio("search", query)
                .flatMap(s -> Observable.just(VkmdUtils.getTrackListFromSearchPageCode(uid, s)));
    }

    @Override
    protected String provideBaseURL() {
        return Constants.BASE_URL;
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
