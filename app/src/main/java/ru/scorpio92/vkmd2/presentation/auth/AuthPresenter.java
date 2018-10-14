package ru.scorpio92.vkmd2.presentation.auth;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.tools.Logger;
import ru.scorpio92.vkmd2.tools.NetworkUtils;

public class AuthPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private Disposable permissionsDisposable;
    private Observable<Boolean> rxPermissionsObservable;
    private ICookieDataSource cookieDataSource;
    private boolean userReadAttention;

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
                                        if (NetworkUtils.checkConnection(getView().getViewContext())) {
                                            NetworkUtils.clearCookies(getView().getViewContext());
                                            getView().loadVkPage();
                                        } else {
                                            onBadConnection();
                                        }
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
                getView().showVkPage();
                if (!userReadAttention) {
                    getView().showAttentionDialog();
                }
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
    public void onBadConnection() {
        if (checkViewState()) {
            getView().hideProgress();
            getView().onError(getString(R.string.error_no_connection));
        }
    }

    @Override
    public void onNotAuthPageLoaded() {
        if (checkViewState()) {
            getView().hideProgress();
            getView().onError(getString(R.string.error_not_auth_page));
        }
    }

    @Override
    public void onUserReadAttention() {
        userReadAttention = true;
    }

    @Override
    protected void writeExceptionInLog(Throwable t) {
        Logger.error((Exception) t);
    }

    @Override
    protected void onCustomErrorsHandle(@NonNull IContract.View view, @NonNull Exception e) {
        view.onError(provideDefaultErrorMsg());
    }

    @Override
    public void onDestroy() {
        if (permissionsDisposable != null && !permissionsDisposable.isDisposed()) {
            permissionsDisposable.dispose();
            permissionsDisposable = null;
        }
        rxPermissionsObservable = null;
        super.onDestroy();
    }
}
