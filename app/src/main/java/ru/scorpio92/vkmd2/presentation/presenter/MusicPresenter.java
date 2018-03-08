package ru.scorpio92.vkmd2.presentation.presenter;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.GetOnlineTracksUsecase;
import ru.scorpio92.vkmd2.domain.usecase.GetSavedTrackListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.GetTrackListFromDBUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveOfflineSearchUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IAbstractUsecase;
import ru.scorpio92.vkmd2.presentation.presenter.base.AbstractPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.IMusicPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.IMusicActivity;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;


public class MusicPresenter extends AbstractPresenter<IMusicActivity> implements IMusicPresenter {

    private IAbstractUsecase getOnlineTracksUsecase;
    private IAbstractUsecase saveOfflineSearchUsecase;

    public MusicPresenter(IMusicActivity view) {
        super(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelUsecases();
    }

    @Override
    public void getTrackList() {
        getView().showProgress(true);

        GetTrackListFromDBUsecase getTrackListFromDBUsecase = new GetTrackListFromDBUsecase(new GetTrackListFromDBUsecase.UsecaseCallback() {
            @Override
            public void onComplete(List<Track> trackList) {
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showTrackList(trackList);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showToast("Что-то пошло не так...");
                }
            }
        });
        getTrackListFromDBUsecase.execute();
    }

    @Override
    public void getSavedTrackList() {
        getView().showProgress(true);

        GetSavedTrackListUsecase getSavedTrackListUsecase = new GetSavedTrackListUsecase(new GetSavedTrackListUsecase.UsecaseCallback() {
            @Override
            public void onComplete(List<Track> trackList) {
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showTrackList(trackList);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showToast("Что-то пошло не так...");
                }
            }
        });
        getSavedTrackListUsecase.execute();
    }

    @Override
    public void getOnlineTracks(CharSequence searchQuery) {
        String searchString = searchQuery.toString().trim();
        if(searchString.length() >= 3) {
            try {
                cancelUsecases();
                String uid = LocalStorage.getDataFromFile(getView().getViewContext(), LocalStorage.USER_ID_STORAGE);
                String cookie = LocalStorage.getDataFromFile(getView().getViewContext(), LocalStorage.COOKIE_STORAGE);
                getOnlineTracksUsecase = new GetOnlineTracksUsecase(uid, cookie, searchString, new GetOnlineTracksUsecase.UsecaseCallback() {
                    @Override
                    public void onComplete(List<Track> trackList) {
                        Logger.log("online tracks: " + trackList.size());
                        if (checkViewState()) {
                            getView().showTrackList(trackList);
                            getView().showOnlineSearchProgress(false);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.error(e);
                        if (checkViewState()) {
                            getView().showToast("Что-то пошло не так...");
                            getView().showOnlineSearchProgress(false);
                        }
                    }
                });

                getView().showOnlineSearchProgress(true);

                getOnlineTracksUsecase.execute();
            } catch (Exception e) {
                Logger.error(e);
                getView().showOnlineSearchProgress(false);
            }
        }
    }

    @Override
    public void saveOfflineSearch(List<String> trackIdList) {
        cancelUsecases();

        saveOfflineSearchUsecase = new SaveOfflineSearchUsecase(trackIdList, new SaveOfflineSearchUsecase.UsecaseCallback() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
            }
        });
        saveOfflineSearchUsecase.execute();
    }

    @Override
    public void sendTracksForDownload(List<String> trackIdList) {
        getView().showPrepareForDownload();

        SaveDownloadListUsecase saveDownloadListUsecase = new SaveDownloadListUsecase(trackIdList, new SaveDownloadListUsecase.UsecaseCallback() {
            @Override
            public void onComplete() {
                if(getView() != null)
                    getView().startDownloadService();
            }

            @Override
            public void onError(Exception e) {
                Logger.error(e);
            }
        });
        saveDownloadListUsecase.execute();
    }

    private void cancelUsecases() {
        if(getOnlineTracksUsecase != null)
            getOnlineTracksUsecase.cancel();
        if(saveOfflineSearchUsecase != null)
            saveOfflineSearchUsecase.cancel();
    }
}
