package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Сохранение треков в специальной таблице для последующего скачивания
 */
public class SaveDownloadListUsecase extends AbstractUsecase {

    private List<String> trackIdList;
    private UsecaseCallback callback;

    public SaveDownloadListUsecase(List<String> trackIdList, UsecaseCallback callback) {
        this.trackIdList = trackIdList;
        this.callback = callback;
    }

    @Override
    protected IExecutor provideExecutor() {
        return ThreadExecutor.getInstance(true);
    }

    @Override
    public void run() {
        try {
            List<CachedTrack> downloadFiles = new ArrayList<>();
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

            runOnUI(() -> callback.onComplete());
        } catch (Exception e) {
            runOnUI(() -> {
                if (callback != null)
                    callback.onError(e);
            });
        }
    }

    @Override
    protected void onInterrupt() {
        callback = null;
    }

    public interface UsecaseCallback extends IUsecaseBaseCallback {
        void onComplete();
    }
}
