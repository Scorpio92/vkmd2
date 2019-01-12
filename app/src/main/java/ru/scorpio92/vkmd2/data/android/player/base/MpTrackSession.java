package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Сущность медиа-сессии предоставляющая информацию о:
 * 1) текущем проигрываемом треке
 * 2) текущих настройках плеера
 * 3) текущей позиции трека (сколько времени проиграно)
 * 4) проигрывается трек в данный момент или нет
 */
public class MpTrackSession {

    private MpTrack track;
    private MpSettings mpSettings;
    private int trackCurrentPosition;
    private boolean isPlaying;

    private MpTrackSession(MpTrack track, MpSettings mpSettings) {
        this.track = track;
        this.mpSettings = mpSettings;
        this.trackCurrentPosition = 0;
        this.isPlaying = false;
    }

    public static MpTrackSession create(MpTrack track, MpSettings mpSettings) {
        return new MpTrackSession(track, mpSettings);
    }

    public synchronized void update(MpSettings mpSettings) {
        this.mpSettings = mpSettings;
    }

    public synchronized void update(int trackCurrentPosition, boolean isPlaying) {
        this.trackCurrentPosition = trackCurrentPosition;
        this.isPlaying = isPlaying;
    }

    public MpTrack getTrack() {
        return track;
    }

    public synchronized MpSettings getFeatures() {
        return mpSettings;
    }

    public synchronized int getTrackCurrentPosition() {
        return trackCurrentPosition;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }
}
