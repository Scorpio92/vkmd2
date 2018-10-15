package ru.scorpio92.vkmd2.presentation.main.tracklist;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUseCase;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.presentation.entity.converter.TrackConverter;
import ru.scorpio92.vkmd2.tools.Logger;

public class TrackListPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private GetAccountTracksUseCase getAccountTracksUseCase;

    public TrackListPresenter(@NonNull IContract.View mView,
                              GetAccountTracksUseCase getAccountTracksUseCase) {
        super(mView);
        this.getAccountTracksUseCase = getAccountTracksUseCase;
    }

    @Override
    public void onPostCreate() {
        getTrackList();
    }

    @Override
    public void getTrackList() {
        getAccountTracksUseCase.execute(new DisposableObserver<List<Track>>() {
            @Override
            protected void onStart() {
                if (checkViewState()) {
                    getView().showProgress();
                    getView().clearTrackList();
                }
            }

            @Override
            public void onNext(List<Track> trackList) {
                if (checkViewState()) {
                    getView().renderTrackList(TrackConverter.convertTrackListToUiTrackList(trackList));
                }
            }

            @Override
            public void onError(Throwable e) {
                handleErrors(e);
            }

            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().hideProgress();
                }
            }
        });
    }

    @Override
    protected void writeExceptionInLog(Throwable t) {
        Logger.error((Exception) t);
    }

    @Override
    protected String provideDefaultErrorMsg() {
        return super.provideDefaultErrorMsg();
    }

    @Override
    protected void onCustomErrorsHandle(@NonNull IContract.View view, @NonNull Exception e) {
        super.onCustomErrorsHandle(view, e);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getAccountTracksUseCase != null) {
            getAccountTracksUseCase.cancel();
        }
    }
}
