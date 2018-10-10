package ru.scorpio92.vkmd2.presentation.sync;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.presentation.base.BasePresenter;

public class SyncPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    public SyncPresenter(@NonNull IContract.View mView) {
        super(mView);
    }

    @Override
    public void getTrackList(int count) {

    }
}
