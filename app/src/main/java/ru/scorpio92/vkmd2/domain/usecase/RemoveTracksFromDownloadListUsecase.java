package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;


/**
 * Удаление списка загрузки
 */
public class RemoveTracksFromDownloadListUsecase extends RxAbstractUsecase {

    private List<String> trackIdList;

    public RemoveTracksFromDownloadListUsecase(List<String> trackIdList) {
        this.trackIdList = trackIdList;
    }

    @Override
    protected Observable provideObservable() {
        return Observable.fromCallable((Callable<Object>) () -> {
            AppDatabase.getInstance().cacheDAO().removeFromDownloadList(trackIdList);
            return Observable.empty();
        }).subscribeOn(provideSubscribeScheduler());
    }
}