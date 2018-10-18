package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.presentation.base.BasePresenter;

public class PlayerPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    PlayerPresenter(@NonNull IContract.View mView) {
        super(mView);
    }

    @Override
    public void previous() {

    }

    @Override
    public void playOrPause() {

    }

    @Override
    public void next() {

    }

    @Override
    public void onLoopPressed() {

    }

    @Override
    public void onRandomPressed() {

    }
}
