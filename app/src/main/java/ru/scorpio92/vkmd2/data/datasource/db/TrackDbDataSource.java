package ru.scorpio92.vkmd2.data.datasource.db;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.datasource.db.base.AbstractDatabaseDataSource;
import ru.scorpio92.vkmd2.data.datasource.db.dao.AppDatabase;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;

public class TrackDbDataSource extends AbstractDatabaseDataSource<AppDatabase, Track>
        implements ITrackDataSource.LocalDB {

    @Override
    public Flowable<List<Track>> getSavedTrackList() {
        /*return getDatabaseRx().flatMapMaybe(appDatabase -> Maybe.fromCallable(() ->
                Observable.fromIterable(appDatabase.cacheDAO().getSavedTracks())
                        .map(cachedTrack -> {
                            Track track = new Track();
                            track.setTrackId(cachedTrack.getTrackId());
                            track.setUserId(cachedTrack.getUserId());
                            track.setArtist(cachedTrack.getArtist());
                            track.setName(cachedTrack.getName());
                            track.setDuration(cachedTrack.getDuration());
                            track.setUrlAudio(cachedTrack.getUrlAudio());
                            track.setUrlImage(cachedTrack.getUrlImage());
                            track.setStatus(cachedTrack.getStatus());
                            track.setSavedPath(cachedTrack.getSavedPath());
                            return track;
                        }).toList().blockingGet())
                .onErrorComplete());*/
        return Flowable.empty();
    }

    @Override
    public Completable saveTrack(Track track) {
        return getDatabaseRx().flatMapCompletable(appDatabase -> Completable.fromAction(() -> {
            //appDatabase.cacheDAO()
        }));
    }
}
