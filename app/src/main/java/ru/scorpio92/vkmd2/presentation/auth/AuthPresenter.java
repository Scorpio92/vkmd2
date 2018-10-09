package ru.scorpio92.vkmd2.presentation.auth;

import android.Manifest;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

public class AuthPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private Disposable permissionsDisposable;

    public AuthPresenter(@NonNull IContract.View mView) {
        super(mView);
    }

    @Override
    public void onPostCreate() {
        if (checkViewState())
            permissionsDisposable = new RxPermissions(getView().getActivity())
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS)
                    .subscribe(granted -> {
                        if (granted) {
                            try {
                                if (checkViewState()) {
                                    if (LocalStorage.fileExist(getView().getViewContext(), LocalStorage.COOKIE_STORAGE)) {
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
                if (!LocalStorage.fileExist(getView().getViewContext(), LocalStorage.LOGIN_DIALOG_FLAG)) {
                    getView().showAttentionDialog();
                }
                getView().showVkPage();
            } catch (Exception e) {
                handleErrors(e);
            }
        }

    }

    @Override
    public void onCookieReady(String cookie) {
        try {
            LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.COOKIE_STORAGE, cookie);
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
        try {
            LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.LOGIN_DIALOG_FLAG, "");
        } catch (Exception e) {
            handleErrors(e);
        }
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
