package ru.scorpio92.vkmd2.presentation.sync;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.SyncTracksUseCase;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;

public class SyncPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private SyncTracksUseCase mSyncTracksUseCase;

    public SyncPresenter(@NonNull IContract.View mView,
                         @NonNull SyncTracksUseCase syncTracksUseCase) {
        super(mView);
        this.mSyncTracksUseCase = syncTracksUseCase;
    }

    @Override
    public void synchronize(int count) {
        mSyncTracksUseCase.execute(count, new DisposableObserver<List<Track>>() {
            @Override
            protected void onStart() {
                if(checkViewState()) {
                    getView().showProgress();
                }
            }

            @Override
            public void onNext(List<Track> tracks) {

            }

            @Override
            public void onError(Throwable e) {
                handleErrors(e);
                if(checkViewState()) {
                    getView().hideProgress();
                }
            }

            @Override
            public void onComplete() {
                if(checkViewState()) {
                    getView().hideProgress();
                    getView().showMusicActivity();
                }
            }
        });
    }

    @Override
    protected void onCustomErrorsHandle(@NonNull IContract.View view, @NonNull Exception e) {
        view.onError(provideDefaultErrorMsg());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSyncTracksUseCase != null)
            mSyncTracksUseCase.cancel();
    }
}
