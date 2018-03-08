package ru.scorpio92.vkmd2.presentation.presenter.base;

import java.util.List;


public interface IDownloadManagerPresenter extends IBasePresenter {
    void getDownloadList();

    void sendTracksForDownload(List<String> trackIdList);

    void removeTracksFromDownloadList(List<String> trackIdList);
}
