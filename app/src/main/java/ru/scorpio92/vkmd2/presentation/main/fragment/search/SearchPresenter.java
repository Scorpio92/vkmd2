package ru.scorpio92.vkmd2.presentation.main.fragment.search;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.presentation.base.BasePresenter;

public class SearchPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    public SearchPresenter(@NonNull IContract.View mView) {
        super(mView);
    }
}
