package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Получение списка сохраненных аудиозаписей
 */
public class GetSavedTrackListUsecase extends AbstractUsecase {

    private UsecaseCallback callback;

    public GetSavedTrackListUsecase(UsecaseCallback callback) {
        this.callback = callback;
    }

    @Override
    protected IExecutor provideExecutor() {
        return ThreadExecutor.getInstance(true);
    }

    @Override
    public void run() {
        try {
            List<CachedTrack> cachedTrackList = AppDatabase.getInstance().cacheDAO().getSavedTracks();
            List<Track> trackList = new ArrayList<>();
            for (CachedTrack cachedTrack : cachedTrackList)
                trackList.add(VkmdUtils.convertCachedTrackToBase(cachedTrack));

            if (callback != null) {
                runOnUI(() -> {
                    if (callback != null)
                        callback.onComplete(trackList);
                });
            }
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
        void onComplete(List<Track> trackList);
    }
}
