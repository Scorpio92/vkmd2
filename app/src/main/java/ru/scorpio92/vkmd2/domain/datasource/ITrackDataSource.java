package ru.scorpio92.vkmd2.domain.datasource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.Track;

public interface ITrackDataSource {

    Maybe<List<Track>> getTrackList();

    Completable clean();

    interface Remote {

        Single<List<Track>> getAccountAudio(int offset);

        Single<List<Track>> searchAudio(String uid, String query);
    }

    interface LocalDB {

        Maybe<List<Track>> getSavedTrackList();

        Completable saveTrack(Track track);
    }

    interface LocalInMemory {

        Maybe<List<Track>> getTrackList();

        Completable saveTrackList(List<Track> tracks);

        Completable clean();
    }
}
