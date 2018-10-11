package ru.scorpio92.vkmd2.data.datasource.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;

public class TracksMemoryCacheDataSource implements ITrackDataSource.LocalInMemory {

    private static final SoftReference<List<Track>> sData = new SoftReference<>(new ArrayList<>());

    @Override
    public Flowable<List<Track>> getTrackList() {
        return Flowable.just(new ArrayList<>(sData.get()));
    }

    @Override
    public Completable addTrackList(List<Track> tracks) {
        return Completable.fromAction(() -> {
            //sData.get().clear();
            sData.get().addAll(tracks);
        });
    }

    @Override
    public Completable clean() {
        return Completable.fromAction(() -> {
            sData.get().clear();
            //sData.clear();
        });
    }
}
