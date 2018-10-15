package ru.scorpio92.vkmd2.presentation.main.tracklist;

import java.util.List;

import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;
import ru.scorpio92.vkmd2.presentation.entity.UiTrack;

public interface IContract {

    interface View extends IBaseView {

        void clearTrackList();

        void renderTrackList(List<UiTrack> trackList);
    }

    interface Presenter extends IBasePresenter {

        void getTrackList();
    }
}
