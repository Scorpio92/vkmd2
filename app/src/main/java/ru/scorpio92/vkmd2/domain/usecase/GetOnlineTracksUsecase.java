package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.old.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.GetAudioRepo;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Поиск аудиозаписей онлайн
 */
public class GetOnlineTracksUsecase extends RxAbstractUsecase<List<Track>> {

    /**
     * id пользователя под которым выполняется поиск
     */
    private String uid;
    private String cookie;
    /**
     * ключевая фраза (поисковый запрос)
     */
    private String searchQuery;

    public GetOnlineTracksUsecase(String uid, String cookie, String searchQuery) {
        this.uid = uid;
        this.cookie = cookie;
        this.searchQuery = searchQuery;
    }

    @Override
    protected Observable<List<Track>> provideObservable() {
        return new GetAudioRepo(cookie).getSearchAudio(uid, searchQuery)
                .flatMap(onlineTracks -> {
                    AppDatabase.getInstance().onlineTrackDAO().saveTrackList(onlineTracks);

                    List<Track> trackList = new ArrayList<>();

                    for (OnlineTrack onlineTrack : onlineTracks) {
                        CachedTrack cachedTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(onlineTrack.getTrackId());
                        Track track = VkmdUtils.convertOnlineTrackToBase(onlineTrack);
                        if (cachedTrack != null) {
                            track.setSaved(cachedTrack.isSaved());
                            track.setSavedPath(cachedTrack.getSavedPath());
                        }
                        trackList.add(track);
                    }

                    return Observable.just(trackList);
                })
                .subscribeOn(provideSubscribeScheduler());
    }
}
