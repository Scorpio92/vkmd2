package ru.scorpio92.vkmd2.domain.datasource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import ru.scorpio92.vkmd2.domain.entity.Track;

public interface ITracksMemoryCacheDataSource {

    Maybe<List<Track>> getTrackList();

    Completable saveTrackList(List<Track> tracks);

    Completable clean();
}
