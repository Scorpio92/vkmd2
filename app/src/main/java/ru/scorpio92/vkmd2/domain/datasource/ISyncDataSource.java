package ru.scorpio92.vkmd2.domain.datasource;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.SyncProperties;

public interface ISyncDataSource {

    /**
     * Сохраняем кол-во аудио для последующей синхронизации (в случае ручной синхронизации)
     * @param count кол-во треков
     */
    Completable saveSyncCount(int count);

    /**
     * Обновляем время последней сихронизации (в случае автосинхронизации)
     */
    Completable refreshSyncTime();

    Single<SyncProperties> getSyncProperties();
}
