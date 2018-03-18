package ru.scorpio92.vkmd2.presentation.presenter;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.domain.usecase.GetOnlineTracksUsecase;
import ru.scorpio92.vkmd2.domain.usecase.GetSavedTrackListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.GetTrackListFromDBUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveDownloadListUsecase;
import ru.scorpio92.vkmd2.domain.usecase.SaveOfflineSearchUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.CompletableObserver;
import ru.scorpio92.vkmd2.domain.usecase.base.ErrorObserver;
import ru.scorpio92.vkmd2.domain.usecase.base.IAbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.SingleObserver;
import ru.scorpio92.vkmd2.presentation.presenter.base.AbstractPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.IMusicPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.IMusicActivity;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;


public class MusicPresenter extends AbstractPresenter<IMusicActivity> implements IMusicPresenter {

    private IAbstractUsecase getOnlineTracksUsecase;
    private GetTrackListFromDBUsecase getTrackListFromDBUsecase;
    private GetSavedTrackListUsecase getSavedTrackListUsecase;
    private SaveOfflineSearchUsecase saveOfflineSearchUsecase;
    private SaveDownloadListUsecase saveDownloadListUsecase;

    public MusicPresenter(IMusicActivity view) {
        super(view);
        getTrackListFromDBUsecase = new GetTrackListFromDBUsecase();
        getSavedTrackListUsecase = new GetSavedTrackListUsecase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelUsecases();
    }

    @Override
    public void getTrackList() {
        getView().showProgress(true);

        getTrackListFromDBUsecase.execute(new SingleObserver<List<Track>>() {
            @Override
            public void onNext(List<Track> tracks) {
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showTrackList(tracks);
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showToast("Что-то пошло не так...");
                }
            }
        });
    }

    @Override
    public void getSavedTrackList() {
        getView().showProgress(true);

        getSavedTrackListUsecase.execute(new SingleObserver<List<Track>>() {
            @Override
            public void onNext(List<Track> tracks) {
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showTrackList(tracks);
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
                if (checkViewState()) {
                    getView().showProgress(false);
                    getView().showToast("Что-то пошло не так...");
                }
            }
        });
    }

    @Override
    public void getOnlineTracks(CharSequence searchQuery) {
        String searchString = searchQuery.toString().trim();
        if (searchString.length() >= 3) {
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

        saveOfflineSearchUsecase = new SaveOfflineSearchUsecase(trackIdList);
        saveOfflineSearchUsecase.execute(new ErrorObserver() {
            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
            }
        });
    }

    @Override
    public void sendTracksForDownload(List<String> trackIdList) {
        getView().showPrepareForDownload();

        saveDownloadListUsecase = new SaveDownloadListUsecase(trackIdList);
        saveDownloadListUsecase.execute(new CompletableObserver() {
            @Override
            public void onError(Throwable e) {
                Logger.error((Exception) e);
            }

            @Override
            public void onComplete() {
                if (getView() != null)
                    getView().startDownloadService();
            }
        });
    }

    private void cancelUsecases() {
        getSavedTrackListUsecase.cancel();
        getTrackListFromDBUsecase.cancel();
        if (getOnlineTracksUsecase != null)
            getOnlineTracksUsecase.cancel();
        if (saveOfflineSearchUsecase != null)
            saveOfflineSearchUsecase.cancel();
        if (saveDownloadListUsecase != null)
            saveDownloadListUsecase.cancel();
    }
}
