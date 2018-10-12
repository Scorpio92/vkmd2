package ru.scorpio92.vkmd2.domain.datasource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.Track;

public interface ITrackDataSource {

    Flowable<List<Track>> getCachedTrackList();

    Flowable<List<Track>> getTrackList(int count);

    Completable clean();

    interface Remote {

        Single<List<Track>> getAccountAudio(String cookie, int offset);

        Single<List<Track>> searchAudio(String cookie, String uid, String query);
    }

    interface LocalDB {

        Flowable<List<Track>> getSavedTrackList();

        Completable saveTrack(Track track);
    }

    interface LocalInMemory {

        Flowable<List<Track>> getTrackList();

        Completable addTrackList(List<Track> tracks);

        Completable clean();
    }
}
