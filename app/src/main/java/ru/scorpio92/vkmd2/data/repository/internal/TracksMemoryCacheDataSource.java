package ru.scorpio92.vkmd2.data.repository.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import ru.scorpio92.vkmd2.domain.datasource.ITracksMemoryCacheDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;

public class TracksMemoryCacheDataSource implements ITracksMemoryCacheDataSource {

    private static final SoftReference<List<Track>> sData = new SoftReference<>(new ArrayList<>());

    @Override
    public Maybe<List<Track>> getTrackList() {
        return Maybe.fromCallable(sData::get);
    }

    @Override
    public Completable saveTrackList(List<Track> tracks) {
        return Completable.fromAction(() -> {
            sData.get().clear();
            sData.get().addAll(tracks);
        });
    }

    @Override
    public Completable clean() {
        return Completable.fromAction(() -> {
            sData.get().clear();
            sData.clear();
        });
    }
}
