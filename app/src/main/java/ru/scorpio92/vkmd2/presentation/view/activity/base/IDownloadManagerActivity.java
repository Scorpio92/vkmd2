package ru.scorpio92.vkmd2.presentation.view.activity.base;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;


public interface IDownloadManagerActivity extends IBaseView {

    void renderDownloadList(List<Track> trackList);

    void onSuccessRemoveFromDownloadList();
}
