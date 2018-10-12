package ru.scorpio92.vkmd2.data.datasource.internal;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.data.datasource.internal.base.AbstractLocalDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ISyncDataSource;
import ru.scorpio92.vkmd2.domain.entity.SyncProperties;
import ru.scorpio92.vkmd2.tools.JsonWorker;

public class SyncDataSource extends AbstractLocalDataSource implements ISyncDataSource {

    @Override
    public Completable saveSyncCount(int count) {
        return Completable.fromAction(() -> {
            String data = JsonWorker.getSerializeJson(new SyncProperties(true, count));
            saveData(data);
        });
    }

    @Override
    public Completable refreshSyncTime() {
        return getSyncProperties().flatMapCompletable(syncProperties -> {
            final int currentTrackCount = syncProperties.getSyncCount();
            return Completable.fromAction(() -> {
                String data = JsonWorker.getSerializeJson(new SyncProperties(false, currentTrackCount));
                saveData(data);
            });
        });
    }

    @Override
    public Single<SyncProperties> getSyncProperties() {
        return Single.fromCallable(() -> JsonWorker.getDeserializeJson(getData(), SyncProperties.class));
    }

    @Override
    protected String provideStoreName() {
        return ".sync_properties";
    }

    @Override
    protected boolean enableEncryption() {
        return false;
    }
}
