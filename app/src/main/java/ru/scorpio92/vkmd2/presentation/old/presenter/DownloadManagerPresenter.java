package ru.scorpio92.vkmd2.presentation.old.presenter;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.GetDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.RemoveTracksFromDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.CompletableObserver;
import ru.scorpio92.vkmd2.domain.usecase.base.SingleObserver;
import ru.scorpio92.vkmd2.presentation.old.presenter.base.AbstractPresenter;
import ru.scorpio92.vkmd2.presentation.old.presenter.base.IDownloadManagerPresenter;
import ru.scorpio92.vkmd2.presentation.old.view.activity.base.IDownloadManagerActivity;
import ru.scorpio92.vkmd2.tools.Logger;


public class DownloadManagerPresenter extends AbstractPresenter<IDownloadManagerActivity> implements IDownloadManagerPresenter {

    private GetDownloadListUsecase getDownloadListUsecase;
    private RemoveTracksFromDownloadListUsecase removeTracksFromDownloadListUsecase;
    private SaveDownloadListUsecase saveDownloadListUsecase;

    public DownloadManagerPresenter(IDownloadManagerActivity view) {
        super(view);
        getDownloadListUsecase = new GetDownloadListUsecase();
    }

    @Override
    public void getDownloadList() {
        getView().showProgress(true);

        getDownloadListUsecase.execute(new SingleObserver<List<Track>>() {
            @Override
            public void onNext(List<Track> tracks) {
                if (checkViewState()) {
                    getView().showProgress(false);

                    if (tracks.isEmpty())
                        getView().showToast("Нечего качать!");
                    else
                        getView().renderDownloadList(tracks);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (checkViewState()) {
                    getView().showProgress(false);
                }
                Logger.error((Exception) e);
            }
        });
    }

    @Override
    public void sendTracksForDownload(List<String> trackIdList) {

        saveDownloadListUsecase = new SaveDownloadListUsecase(trackIdList);
        saveDownloadListUsecase.execute(new CompletableObserver() {
            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
            }

            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().showToast("Выбранные треки помечены к загрузке");
                }
            }
        });
    }

    @Override
    public void removeTracksFromDownloadList(List<String> trackIdList) {
        removeTracksFromDownloadListUsecase = new RemoveTracksFromDownloadListUsecase(trackIdList);
        removeTracksFromDownloadListUsecase.execute(new CompletableObserver() {
            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
            }

            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().onSuccessRemoveFromDownloadList();
                    getView().showToast("Выбранные треки успешно удалены из списка загрузок");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (removeTracksFromDownloadListUsecase != null)
            removeTracksFromDownloadListUsecase.cancel();
        if (saveDownloadListUsecase != null)
            saveDownloadListUsecase.cancel();
        getDownloadListUsecase.cancel();
    }
}
