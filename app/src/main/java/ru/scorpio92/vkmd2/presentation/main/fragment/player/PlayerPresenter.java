package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.domain.datasource.IBroadcastReceiver;
import ru.scorpio92.vkmd2.domain.datasource.IBroadcastSender;
import ru.scorpio92.vkmd2.domain.entity.BroadcastData;
import ru.scorpio92.vkmd2.domain.entity.TrackInfo;
import ru.scorpio92.vkmd2.presentation.base.BasePresenter;
import ru.scorpio92.vkmd2.service.base.AudioServiceContract;
import ru.scorpio92.vkmd2.tools.JsonWorker;
import ru.scorpio92.vkmd2.tools.Logger;

public class PlayerPresenter extends BasePresenter<IContract.View> implements IContract.Presenter {

    private IBroadcastSender broadcastSender;
    private IBroadcastReceiver broadcastReceiver;

    public PlayerPresenter(@NonNull IContract.View mView, IBroadcastSender broadcastSender,
                           IBroadcastReceiver broadcastReceiver) {
        super(mView);
        this.broadcastSender = broadcastSender;
        this.broadcastReceiver = broadcastReceiver;
        this.broadcastReceiver.initialize(broadcastListener);
    }

    @Override
    public void onPostCreate() {
        sendEvent(AudioServiceContract.SenderEvent.GET_CURRENT_TRACK);
    }

    @Override
    public void onDestroy() {
        if(broadcastReceiver != null) {
            broadcastReceiver.finish();
        }
        super.onDestroy();
    }

    @Override
    protected void writeExceptionInLog(Throwable t) {
        Logger.error((Exception) t);
    }

    @Override
    protected String provideDefaultErrorMsg() {
        return super.provideDefaultErrorMsg();
    }

    @Override
    protected void onCustomErrorsHandle(@NonNull IContract.View view, @NonNull Exception e) {
        view.showToast(provideDefaultErrorMsg());
    }

    @Override
    public void previous() {
        getView().onTrackLoading();
        sendEvent(AudioServiceContract.SenderEvent.PREV);
    }

    @Override
    public void playOrPause() {
        sendEvent(AudioServiceContract.SenderEvent.PLAY_OR_PAUSE);
    }

    @Override
    public void next() {
        getView().onTrackLoading();
        sendEvent(AudioServiceContract.SenderEvent.NEXT);
    }

    @Override
    public void seekTo(int position) {
        BroadcastData broadcastData = new BroadcastData(
                AudioServiceContract.SenderEvent.SEEK_TO.name(),
                String.valueOf(position),
                Integer.class
        );
        broadcastSender.sendBroadcastMessage(broadcastData);
    }

    @Override
    public void onLoopPressed() {
        sendEvent(AudioServiceContract.SenderEvent.LOOP_FEATURE_CHECKED);
    }

    @Override
    public void onRandomPressed() {
        sendEvent(AudioServiceContract.SenderEvent.RANDOM_FEATURE_CHECKED);
    }

    private void sendEvent(AudioServiceContract.SenderEvent senderEvent) {
        broadcastSender.sendBroadcastEvent(senderEvent.name());
    }

    private IBroadcastReceiver.Listener broadcastListener = new IBroadcastReceiver.Listener() {
        @Override
        public void onResult(BroadcastData broadcastData) {
            if(checkViewState()) {
                AudioServiceContract.ReceiverEvent receiverEvent =
                        Enum.valueOf(AudioServiceContract.ReceiverEvent.class, broadcastData.getEvent());
                String data = broadcastData.getSerializedData();
                switch (receiverEvent) {
                    case TRACK_UPDATE:
                        TrackInfo trackInfo = JsonWorker.getDeserializeJson(data, TrackInfo.class);
                        getView().onTrackRefresh(trackInfo);
                        break;
                    case PLAY:
                        getView().onTrackLoadingComplete();
                        getView().onPlay();
                        break;
                    case PAUSE:
                        getView().onPauseTrack();
                        break;
                    case LOOP_ENABLED:
                        getView().onLoopEnabled(true);
                        break;
                    case LOOP_DISABLED:
                        getView().onLoopEnabled(false);
                        break;
                    case RANDOM_ENABLED:
                        getView().onRandomEnabled(true);
                        break;
                    case RANDOM_DISABLED:
                        getView().onRandomEnabled(false);
                        break;
                    case PLAYER_STOPPED:
                        getView().onStopPlaying();
                        break;
                    case ERROR:
                        getView().onTrackLoadingComplete();
                        Exception exception = JsonWorker.getDeserializeJson(data, Exception.class);
                        handleErrors(exception);
                        break;
                }
            }
        }

        @Override
        public void onError(Exception e) {
            handleErrors(e);
        }
    };
}
