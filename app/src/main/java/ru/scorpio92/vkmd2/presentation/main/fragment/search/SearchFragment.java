package ru.scorpio92.vkmd2.presentation.main.fragment.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.di.PresenterInjection;
import ru.scorpio92.vkmd2.presentation.base.BaseFragment;

public class SearchFragment extends BaseFragment<IContract.Presenter> implements IContract.View {

    @Nullable
    @Override
    protected IContract.Presenter bindPresenter() {
        return PresenterInjection.provideSearchPresenter(this);
    }

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initUI(@NonNull View view) {
        super.initUI(view);
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
    }

    @Override
    public void onError(@NonNull String error) {
        super.onError(error);
    }
}
