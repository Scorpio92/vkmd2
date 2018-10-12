package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.domain.datasource.ISyncDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.domain.entity.exception.BadTrackCountException;

import static ru.scorpio92.vkmd2.domain.base.ValidateUtils.throwIfNull;


/**
 * Синхронизация списка аудиозаписей
 */
public class SyncTracksUseCase extends AbstractUseCase<List<Track>> {

    private int mSyncCount;
    private ISyncDataSource mSyncDataSource;
    private ITrackDataSource mTrackRepository;

    public SyncTracksUseCase(ISyncDataSource syncDataSource, ITrackDataSource trackRepository) {
        this.mSyncDataSource = syncDataSource;
        this.mTrackRepository = trackRepository;
    }

    @Override
    protected void checkPreconditions(DisposableObserver<List<Track>> observer) throws Exception {
        if (mSyncCount < 1)
            throw new BadTrackCountException();
        throwIfNull(mSyncDataSource);
        throwIfNull(mTrackRepository);
    }

    @Override
    public Observable<List<Track>> provideObservable() throws Exception {
        return mTrackRepository.getTrackList(mSyncCount)
                .toObservable()
                .doOnComplete(() -> mSyncDataSource.saveSyncCount(mSyncCount).blockingAwait());
    }

    public void execute(int syncCount, DisposableObserver<List<Track>> observer) {
        this.mSyncCount = syncCount;
        execute(observer);
    }
}
