package ru.scorpio92.vkmd2.presentation.auth;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.domain.entity.AuthInfo;
import ru.scorpio92.vkmd2.domain.usecase.GetAuthInfoUseCase;
import ru.scorpio92.vkmd2.domain.usecase.SaveCookieUseCase;
import ru.scorpio92.vkmd2.domain.usecase.base.CompletableObserver;
import ru.scorpio92.vkmd2.domain.usecase.base.SingleObserver;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.tools.Logger;

public class AuthPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private GetAuthInfoUseCase getAuthInfoUseCase;
    private SaveCookieUseCase saveCookieUseCase;
    private boolean userReadAttention;

    public AuthPresenter(@NonNull IContract.View mView, GetAuthInfoUseCase getAuthInfoUseCase,
                         SaveCookieUseCase saveCookieUseCase) {
        super(mView);
        this.getAuthInfoUseCase = getAuthInfoUseCase;
        this.saveCookieUseCase = saveCookieUseCase;
    }

    @Override
    public void onPostCreate() {
        getAuthInfoUseCase.execute(new SingleObserver<AuthInfo>() {
            @Override
            public void onNext(AuthInfo authInfo) {
                if (checkViewState()) {
                    if (authInfo.isPermissionsGranted()) {
                        if (authInfo.isUserIsAuthorized()) {
                            getView().showMainActivity();
                        } else {
                            getView().loadVkPage();
                        }
                    } else {
                        getView().onPermissionNotGranted();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                handleErrors(e);
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
        saveCookieUseCase.execute(cookie, new CompletableObserver() {
            @Override
            public void onError(Throwable e) {
                handleErrors(e);
            }

            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().showSyncActivity();
                }
            }
        });
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
        if (getAuthInfoUseCase != null) {
            getAuthInfoUseCase.cancel();
        }
        if (saveCookieUseCase != null) {
            saveCookieUseCase.cancel();
        }
        super.onDestroy();
    }
}
