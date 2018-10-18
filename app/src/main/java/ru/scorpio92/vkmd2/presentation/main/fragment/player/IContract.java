package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;

public interface IContract {

    interface View extends IBaseView {

        void onTrackLoading();

        void onTrackLoadingComplete();

        void onPlay();

        void onPauseTrack();

        void onTrackRefresh(@NonNull Track track);

        void onLoopEnabled(boolean enabled);

        void onRandomEnabled(boolean enabled);

        void showToast(@NonNull String error);
    }

    interface Presenter extends IBasePresenter {

        void previous();

        void playOrPause();

        void next();

        void onLoopPressed();

        void onRandomPressed();
    }
}
