package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;


/**
 * Удаление списка загрузки
 */
public class RemoveTracksFromDownloadListUsecase extends AbstractUsecase {

    private List<String> trackIdList;
    private UsecaseCallback callback;

    public RemoveTracksFromDownloadListUsecase(List<String> trackIdList, UsecaseCallback callback) {
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
            AppDatabase.getInstance().cacheDAO().removeFromDownloadList(trackIdList);
            runOnUI(() -> callback.onComplete());
        } catch (Exception e) {
            runOnUI(() -> {
                if(callback != null)
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