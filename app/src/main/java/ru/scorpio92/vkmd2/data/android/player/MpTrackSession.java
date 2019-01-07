package ru.scorpio92.vkmd2.data.android.player;

import java.util.List;

/**
 * Сущность медиа-сессии предоставляющая информацию о:
 * 1) текущем проигрываемом треке
 * 2) текущих настройках плеера
 * 3) текущей позиции трека (сколько времени проиграно)
 * 4) проигрывается трек в данный момент или нет
 */
public class MpTrackSession {

    private MpTrack track;
    private List<MpFeature> features;
    private int trackCurrentPosition;
    private boolean isPlaying;

    private MpTrackSession(MpTrack track, List<MpFeature> features) {
        this.track = track;
        this.features = features;
        this.trackCurrentPosition = 0;
        this.isPlaying = false;
    }

    public static MpTrackSession create(MpTrack track, List<MpFeature> features) {
        return new MpTrackSession(track, features);
    }

    public synchronized void update(List<MpFeature> features) {
        this.features = features;
    }

    public synchronized void update(int trackCurrentPosition, boolean isPlaying) {
        this.trackCurrentPosition = trackCurrentPosition;
        this.isPlaying = isPlaying;
    }

    public MpTrack getTrack() {
        return track;
    }

    public synchronized List<MpFeature> getFeatures() {
        return features;
    }

    public synchronized int getTrackCurrentPosition() {
        return trackCurrentPosition;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }
}
