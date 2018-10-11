package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.datasource.db.dao.AppDatabase;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Сохранение треков в специальной таблице для последующего скачивания
 */
public class SaveDownloadListUsecase extends RxAbstractUsecase {

    private List<String> trackIdList;

    public SaveDownloadListUsecase(List<String> trackIdList) {
        this.trackIdList = trackIdList;
    }

    @Override
    protected Observable provideObservable() {
        return Observable.fromCallable((Callable<Object>) () -> {
            List<CachedTrack> downloadFiles = new ArrayList<>();

            //получаем индекс для вставки
            int idx = AppDatabase.getInstance().cacheDAO().getLastRowId() + 1;

            //ищем объект для скачивания в нужной таблице
            for (String trackId : trackIdList) {
                //сначала смотрим основную таблицу
                Track track = AppDatabase.getInstance().trackDAO().getTrackByTrackId(trackId);
                if (track != null) {
                    CachedTrack cachedTrack = VkmdUtils.convertTrackToCached(track);
                    cachedTrack.setId(idx);
                    downloadFiles.add(cachedTrack);
                    idx++;
                } else {
                    //если в основной таблице ничего не найдено, идем в таблицу онлайн поиска
                    OnlineTrack onlineTrack = AppDatabase.getInstance().onlineTrackDAO().getTrackByTrackId(trackId);
                    if (onlineTrack != null) {
                        CachedTrack cachedTrack = VkmdUtils.convertOnlineTrackToCached(onlineTrack);
                        cachedTrack.setId(idx);
                        downloadFiles.add(cachedTrack);
                        idx++;
                    }
                }
            }

            if (!downloadFiles.isEmpty())
                AppDatabase.getInstance().cacheDAO().saveDownloadList(downloadFiles);

            return Observable.empty();

        }).subscribeOn(provideSubscribeScheduler());
    }
}
