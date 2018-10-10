package ru.scorpio92.vkmd2.domain.datasource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.Track;

public interface IDbTracksDataSource {

    Single<List<Track>> getTrackList();

    Completable saveTrack(Track track);
}
