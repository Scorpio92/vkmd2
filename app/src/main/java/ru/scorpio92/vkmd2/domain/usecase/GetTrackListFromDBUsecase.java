package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;


/**
 * Получение аудиозаписей с аккаунта из БД
 */
public class GetTrackListFromDBUsecase extends AbstractUsecase {

    private UsecaseCallback callback;

    public GetTrackListFromDBUsecase(UsecaseCallback callback) {
        this.callback = callback;
    }

    @Override
    protected IExecutor provideExecutor() {
        return ThreadExecutor.getInstance(true);
    }

    @Override
    public void run() {
        try {
            List<Track> trackList = AppDatabase.getInstance().trackDAO().getTrackList();

            for (Track track : trackList) {
                CachedTrack cachedTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(track.getTrackId());
                if (cachedTrack != null) {
                    track.setSaved(cachedTrack.isSaved());
                    track.setSavedPath(cachedTrack.getSavedPath());
                }
            }

            runOnUI(() -> {
                if (callback != null)
                    callback.onComplete(trackList);
            });
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
