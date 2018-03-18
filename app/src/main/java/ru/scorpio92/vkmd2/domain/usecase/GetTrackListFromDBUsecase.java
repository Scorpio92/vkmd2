package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;


/**
 * Получение аудиозаписей с аккаунта из БД
 */
public class GetTrackListFromDBUsecase extends RxAbstractUsecase<List<Track>> {

    @Override
    protected Observable<List<Track>> provideObservable() {
        return Observable.fromCallable(() -> {
            List<Track> trackList = AppDatabase.getInstance().trackDAO().getTrackList();

            for (Track track : trackList) {
                CachedTrack cachedTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(track.getTrackId());
                if (cachedTrack != null) {
                    track.setSaved(cachedTrack.isSaved());
                    track.setSavedPath(cachedTrack.getSavedPath());
                }
            }
            return trackList;
        }).subscribeOn(provideSubscribeScheduler());
    }
}
