package ru.scorpio92.vkmd2.data.android.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import java.io.IOException;

import ru.scorpio92.vkmd2.data.android.player.base.ILegacyPlayer;

public class AndroidAudioPlayer implements ILegacyPlayer,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private Listener listener;

    public AndroidAudioPlayer(Context context, Listener listener) {
        this.listener = listener;

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void start(String trackURI) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(trackURI);
            mediaPlayer.prepareAsync();
        } catch (IOException ioe) {
            if (listener != null) {
                listener.onError(ioe);
            }
        }
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void resume() {
        mediaPlayer.start();
    }

    @Override
    public void seekTo(int positionInMS) {
        mediaPlayer.seekTo(positionInMS);
    }

    @Override
    public void stop() {
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.onPlayCompleted();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case -38:
            case 1:
                mediaPlayer.stop();
                mediaPlayer.reset();
                if(listener != null) {
                    listener.onError(new Exception("Failed to play. Reason code: " + what));
                }
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (listener != null) {
            listener.onPlayStarted();
        }
    }
}
