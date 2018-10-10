package ru.scorpio92.vkmd2.presentation.sync;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUseCase;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;

public class SyncPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private GetAccountTracksUseCase getAccountTracksUseCase;

    public SyncPresenter(@NonNull IContract.View mView,
                         @NonNull GetAccountTracksUseCase getAccountTracksUseCase) {
        super(mView);
        this.getAccountTracksUseCase = getAccountTracksUseCase;
    }

    @Override
    public void getTrackList(int count) {

    }

    @Override
    protected void onCustomErrorsHandle(@NonNull IContract.View view, @NonNull Exception e) {
        view.onError(provideDefaultErrorMsg());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getAccountTracksUseCase != null)
            getAccountTracksUseCase.cancel();
    }
}
