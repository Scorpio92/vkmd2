package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.domain.datasource.ISyncDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;

import static ru.scorpio92.vkmd2.domain.base.ValidateUtils.throwIfNull;


/**
 * Получение списка аудиозаписей с аккаунта пользователя
 */
public class GetAccountTracksUseCase extends AbstractUseCase<List<Track>> {

    private ISyncDataSource mSyncDataSource;
    private ITrackDataSource mTrackRepository;

    public GetAccountTracksUseCase(ISyncDataSource syncDataSource, ITrackDataSource trackRepository) {
        this.mSyncDataSource = syncDataSource;
        this.mTrackRepository = trackRepository;
    }

    @Override
    protected void checkPreconditions(DisposableObserver<List<Track>> observer) throws Exception {
        throwIfNull(mSyncDataSource);
        throwIfNull(mTrackRepository);
    }

    @Override
    public Observable<List<Track>> provideObservable() throws Exception {
        return mSyncDataSource.getSyncProperties().flatMapObservable(syncProperties -> {
            //если была произведена ручная синхронизация (на экране синхронизации)
            if (syncProperties.isManualSync()) {
                //получаем готовый кэш
                return mTrackRepository.getCachedTrackList().toObservable();
            } else {
                //иначе запускаем полный процесс получения треков
                return mTrackRepository.getTrackList(syncProperties.getSyncCount()).toObservable();
            }
        }).doOnComplete(() -> mSyncDataSource.refreshSyncTime().blockingAwait());
    }
}
