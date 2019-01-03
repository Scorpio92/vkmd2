package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.domain.entity.TrackInfo;
import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;

public interface IContract {

    interface View extends IBaseView {

        void onTrackLoading();

        void onTrackLoadingComplete();

        void onPlay();

        void onPauseTrack();

        void onTrackRefresh(@NonNull TrackInfo trackInfo);

        void onLoopEnabled(boolean enabled);

        void onRandomEnabled(boolean enabled);

        void onStopPlaying();

        void showToast(@NonNull String message);
    }

    interface Presenter extends IBasePresenter {

        void previous();

        void playOrPause();

        void next();

        void seekTo(int position);

        void onLoopPressed();

        void onRandomPressed();
    }
}
