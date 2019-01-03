package ru.scorpio92.vkmd2.domain.entity;

/**
 * Информация о текущем проигрываемом треке
 */
public class TrackInfo {

    private String name;
    private String artist;
    private int currentPosition; //ms
    private int duration; //ms

    public TrackInfo(String name, String artist, int currentPosition, int duration) {
        this.name = name;
        this.artist = artist;
        this.currentPosition = currentPosition;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getDuration() {
        return duration;
    }
}
