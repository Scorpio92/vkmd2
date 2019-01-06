package ru.scorpio92.vkmd2.data.android.player;

/**
 * Сущность музыкальной композиции
 */
public class MpTrack {

    private String trackId;
    private String trackName;
    private String trackArtist;
    private int trackDuration;

    public MpTrack(String trackId, String trackName, String trackArtist, int trackDuration) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackDuration = trackDuration;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public int getTrackDuration() {
        return trackDuration;
    }
}
