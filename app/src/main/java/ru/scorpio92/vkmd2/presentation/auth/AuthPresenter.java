package ru.scorpio92.vkmd2.presentation.auth;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.tools.Logger;

public class AuthPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private Disposable permissionsDisposable;
    private Observable<Boolean> rxPermissionsObservable;
    private ICookieDataSource cookieDataSource;

    public AuthPresenter(@NonNull IContract.View mView,
                         @NonNull Observable<Boolean> rxPermissionsObservable,
                         @NonNull ICookieDataSource cookieDataSource) {
        super(mView);
        this.rxPermissionsObservable = rxPermissionsObservable;
        this.cookieDataSource = cookieDataSource;
    }

    @Override
    public void onPostCreate() {
        if (checkViewState())
            permissionsDisposable = rxPermissionsObservable
                    .subscribe(granted -> {
                        if (granted) {
                            try {
                                if (checkViewState()) {
                                    if (cookieDataSource.checkCookieExists().blockingGet()) {
                                        getView().showMusicActivity();
                                    } else {
                                        getView().loadVkPage();
                                    }
                                }
                            } catch (Exception e) {
                                if (checkViewState())
                                    handleErrors(e);
                            }
                        } else {
                            if (checkViewState())
                                getView().onPermissionNotGranted();
                        }
                    });
    }

    @Override
    public void onPageBeginLoading() {
        if (checkViewState())
            getView().showProgress();
    }

    @Override
    public void onAuthPageLoaded() {
        if (checkViewState()) {
            try {
                getView().hideProgress();
                getView().showAttentionDialog();
                getView().showVkPage();
            } catch (Exception e) {
                handleErrors(e);
            }
        }

    }

    @Override
    public void onCookieReady(String cookie) {
        try {
            cookieDataSource.saveCookie(cookie).blockingAwait();
            getView().showSyncActivity();
        } catch (Exception e) {
            handleErrors(e);
        }
    }

    @Override
    public void onError() {
        if (checkViewState()) {
            getView().hideProgress();
            getView().onError(provideDefaultErrorMsg());
        }
    }

    @Override
    public void onUserReadAttention() {

    }

    @Override
    protected void writeExceptionInLog(Throwable t) {
        Logger.error((Exception) t);
    }

    @Override
    public void onDestroy() {
        if (permissionsDisposable != null && !permissionsDisposable.isDisposed())
            permissionsDisposable.dispose();

        super.onDestroy();
    }
}
