package ru.scorpio92.vkmd2.domain.usecase;


import java.io.File;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.App;
import ru.scorpio92.vkmd2.BuildConfig;
import ru.scorpio92.vkmd2.data.repository.network.UpdateRepo;
import ru.scorpio92.vkmd2.domain.usecase.base.RxAbstractUsecase;

public class CheckUpdateUsecase extends RxAbstractUsecase<String> {

    private UpdateRepo updateRepo;

    public CheckUpdateUsecase() {
        updateRepo = new UpdateRepo();
    }

    @Override
    protected Observable<String> provideObservable() {
        return updateRepo.getLastVersion()
                .flatMap(s -> {
                    int currentAppVersion = Integer.valueOf(BuildConfig.VERSION_NAME.replaceAll("\\D+", ""));
                    int lastAppVersion = Integer.valueOf(s.replaceAll("\\D+", ""));
                    if (lastAppVersion > currentAppVersion) {
                        File updateDir = new File(App.APP_DIR + "/" + BuildConfig.UPDATE_FOLDER);
                        if(!updateDir.exists())
                            updateDir.mkdirs();
                        return updateRepo.downloadLastBuild(s);
                    } else {
                        return Observable.just("");
                    }
                })
                .subscribeOn(provideSubscribeScheduler());
    }

    @Override
    public void cancel() {
        super.cancel();
        updateRepo.cancel();
    }
}
