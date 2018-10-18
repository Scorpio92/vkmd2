package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;

public interface IContract {

    interface View extends IBaseView {

        void onPlay();

        void onPauseTrack();

        void onTrackRefresh(Track track);

        void onLoopEnabled(boolean enabled);

        void onRandomEnabled(boolean enabled);
    }

    interface Presenter extends IBasePresenter {

        void previous();

        void playOrPause();

        void next();

        void onLoopPressed();

        void onRandomPressed();
    }
}
