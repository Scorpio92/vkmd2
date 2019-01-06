package ru.scorpio92.vkmd2.data.android.player;

import java.util.List;

/**
 * Сущность медиа-сессии предоставляющая информацию о:
 * 1) текущем проигрываемом треке
 * 2) текущих настройках плеера
 * 3) текущей позиции трека (сколько времени проиграно)
 */
public class MpTrackSession {

    private MpTrack track;
    private List<MpFeature> features;
    private int trackCurrentPosition;
    private boolean isPlaying;

    private MpTrackSession(List<MpFeature> features) {
        this.features = features;
        this.trackCurrentPosition = 0;
        this.isPlaying = false;
    }

    public MpTrackSession create(List<MpFeature> features) {
        return new MpTrackSession(features);
    }

    public synchronized void setTrack(MpTrack track) {
        this.track = track;
    }

    public synchronized void setTrackCurrentPosition(int trackCurrentPosition) {
        this.trackCurrentPosition = trackCurrentPosition;
    }

    public synchronized void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void changeFeatureEnabled(int featureId, boolean enabled) {
        for (MpFeature feature : features) {
            if(featureId == feature.getId()) {
                feature.setEnabled(enabled);
            }
        }
    }

    public synchronized MpTrack getTrack() {
        return track;
    }

    public List<MpFeature> getFeatures() {
        return features;
    }

    public synchronized int getTrackCurrentPosition() {
        return trackCurrentPosition;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }
}
