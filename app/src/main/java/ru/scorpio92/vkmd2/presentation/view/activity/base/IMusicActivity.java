package ru.scorpio92.vkmd2.presentation.view.activity.base;


import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;

public interface IMusicActivity extends IBaseView {

    void showUpdateDialog(String path);

    void showTrackList(List<Track> trackList);

    void showMusicFooterInfo(String artist, String trackName, int duration, String imageUrl);

    void showCurrentPlayProgress(int progress);

    void showPrepareForPlay(boolean show);

    void showPauseButton();

    void showPlayButton();

    void showPrepareForDownload();

    void startDownloadService();

    void showOnlineSearchProgress(boolean show);
}
