package ru.scorpio92.vkmd2.presentation.auth;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

public class AuthPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    public AuthPresenter(@NonNull IContract.View mView) {
        super(mView);
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
    public void onAudioPageFinishLoad(String cookie) {
        try {
            LocalStorage.setDataInFile(getView().getViewContext(), LocalStorage.COOKIE_STORAGE, cookie);
        } catch (Exception e) {
            handleErrors(e);
        }
        if(checkViewState())
            getView().showSyncActivity();
    }

    @Override
    public void onError() {
        if(checkViewState())
            getView().onError(provideDefaultErrorMsg());
    }

    @Override
    public void onPermissionGranted() {
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
        super.onDestroy();
    }
}
