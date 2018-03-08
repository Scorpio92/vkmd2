package ru.scorpio92.vkmd2.presentation.presenter;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.GetDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.RemoveTracksFromDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveDownloadListUsecase;
import ru.scorpio92.vkmd2.presentation.presenter.base.AbstractPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.IDownloadManagerPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.IDownloadManagerActivity;
import ru.scorpio92.vkmd2.tools.Logger;


public class DownloadManagerPresenter extends AbstractPresenter<IDownloadManagerActivity> implements IDownloadManagerPresenter {

    public DownloadManagerPresenter(IDownloadManagerActivity view) {
        super(view);
    }

    @Override
    public void getDownloadList() {
        getView().showProgress(true);

        GetDownloadListUsecase getDownloadListUsecase = new GetDownloadListUsecase(new GetDownloadListUsecase.UsecaseCallback() {
            @Override
            public void onComplete(List<Track> trackList) {
                if (checkViewState()) {
                    getView().showProgress(false);

                    if (trackList.isEmpty())
                        getView().showToast("Нечего качать!");
                    else
                        getView().renderDownloadList(trackList);
                }
            }

            @Override
            public void onError(Exception e) {
                if (checkViewState()) {
                    getView().showProgress(false);
                }
                Logger.error(e);
            }
        });
        getDownloadListUsecase.execute();
    }

    @Override
    public void sendTracksForDownload(List<String> trackIdList) {

        SaveDownloadListUsecase saveDownloadListUsecase = new SaveDownloadListUsecase(trackIdList, new SaveDownloadListUsecase.UsecaseCallback() {
            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().showToast("Выбранные треки помечены к загрузке");
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
            }
        });
        saveDownloadListUsecase.execute();
    }

    @Override
    public void removeTracksFromDownloadList(List<String> trackIdList) {
        RemoveTracksFromDownloadListUsecase removeTracksFromDownloadListUsecase = new RemoveTracksFromDownloadListUsecase(trackIdList, new RemoveTracksFromDownloadListUsecase.UsecaseCallback() {
            @Override
            public void onComplete() {
                if (checkViewState()) {
                    getView().onSuccessRemoveFromDownloadList();
                    getView().showToast("Выбранные треки успешно удалены из списка загрузок");
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
            }
        });
        removeTracksFromDownloadListUsecase.execute();
    }
}
