package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.OfflineSearchItem;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;


/**
 * Сохранение результатов офлайн поиска
 */
public class SaveOfflineSearchUsecase extends RxAbstractUsecase {

    private List<String> trackIdList;

    public SaveOfflineSearchUsecase(List<String> trackIdList) {
        this.trackIdList = trackIdList;
    }

    @Override
    protected Observable provideObservable() {
        return Observable.fromCallable((Callable<Object>) () -> {
            List<OfflineSearchItem> offlineSearchItems = new ArrayList<>();
            int idx = 1;
            for (String trackId : trackIdList) {
                offlineSearchItems.add(new OfflineSearchItem(idx, trackId));
                idx++;
            }

            AppDatabase.getInstance().offlineSearchDAO().deleteAll();
            AppDatabase.getInstance().offlineSearchDAO().saveTrackIdList(offlineSearchItems);

            return Observable.empty();

        }).subscribeOn(provideSubscribeScheduler());
    }
}
