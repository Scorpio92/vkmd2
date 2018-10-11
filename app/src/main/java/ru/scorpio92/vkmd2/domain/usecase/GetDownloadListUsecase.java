package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.datasource.db.dao.AppDatabase;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Получение списка аудиозаписей для скачивания
 */
public class GetDownloadListUsecase extends RxAbstractUsecase<List<Track>> {

    @Override
    protected Observable<List<Track>> provideObservable() {
        return Observable.fromCallable(() -> {
            List<CachedTrack> cachedTrackList = AppDatabase.getInstance().cacheDAO().getDownloadList();
            List<Track> trackList = new ArrayList<>();
            for (CachedTrack cachedTrack : cachedTrackList)
                trackList.add(VkmdUtils.convertCachedTrackToBase(cachedTrack));
            return trackList;
        }).subscribeOn(provideSubscribeScheduler());
    }
}
