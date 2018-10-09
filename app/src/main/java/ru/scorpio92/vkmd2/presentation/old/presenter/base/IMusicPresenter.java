package ru.scorpio92.vkmd2.presentation.old.presenter.base;

import java.util.List;


public interface IMusicPresenter extends IBasePresenter {

    void checkForUpdate();

    void getTrackList();

    void getSavedTrackList();

    void getOnlineTracks(CharSequence searchQuery);

    void saveOfflineSearch(List<String> trackIdList);

    void sendTracksForDownload(List<String> trackIdList);
}
