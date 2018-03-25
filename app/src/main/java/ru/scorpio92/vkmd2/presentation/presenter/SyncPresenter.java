package ru.scorpio92.vkmd2.presentation.presenter;

import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUsecase;
import ru.scorpio92.vkmd2.presentation.presenter.base.AbstractPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.ISyncPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.ISyncActivity;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;


public class SyncPresenter extends AbstractPresenter<ISyncActivity> implements ISyncPresenter {

    private GetAccountTracksUsecase usecase;

    public SyncPresenter(ISyncActivity view) {
        super(view);
    }

    @Override
    public void parsePageSourceCode(final String cookie, int count) {
        getView().showProgress(true);

        usecase = new GetAccountTracksUsecase(cookie, count);
        usecase.execute(new DisposableObserver<String>() {
            @Override
            public void onNext(String uid) {
                try {
                    LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.USER_ID_STORAGE, uid);
                } catch (Exception e) {
                    Logger.error(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().onError();
                }
            }

            @Override
            public void onComplete() {
                try {
                    LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.SYNC_TRACKS_COUNT_STORAGE, String.valueOf(count));
                    LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.SYNC_LAST_TIME_STORAGE, String.valueOf(System.currentTimeMillis()));
                } catch (Exception e) {
                    Logger.error(e);
                }

                if (checkViewState()) {
                    getView().showMusicActivity();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usecase != null)
            usecase.cancel();
    }
}
