package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.OfflineSearchItem;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;


/**
 * Сохранение результатов офлайн поиска
 */
public class SaveOfflineSearchUsecase extends AbstractUsecase {

    private List<String> trackIdList;
    private UsecaseCallback callback;

    public SaveOfflineSearchUsecase(List<String> trackIdList, UsecaseCallback callback) {
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
            List<OfflineSearchItem> offlineSearchItems = new ArrayList<>();
            int idx = 1;
            for (String trackId : trackIdList) {
                offlineSearchItems.add(new OfflineSearchItem(idx, trackId));
                idx++;
            }

            AppDatabase.getInstance().offlineSearchDAO().deleteAll();
            AppDatabase.getInstance().offlineSearchDAO().saveTrackIdList(offlineSearchItems);

            runOnUI(() -> {
                if (callback != null)
                    callback.onComplete();
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
        void onComplete();
    }
}
