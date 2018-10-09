package ru.scorpio92.vkmd2.presentation.old.presenter.base;

import ru.scorpio92.vkmd2.presentation.old.view.activity.base.IBaseView;


public abstract class AbstractPresenter<V extends IBaseView> implements IBasePresenter {

    protected V view;

    public AbstractPresenter(V view) {
        this.view = view;
    }

    protected V getView() {
        return view;
    }

    protected boolean checkViewState() {
        return view != null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
