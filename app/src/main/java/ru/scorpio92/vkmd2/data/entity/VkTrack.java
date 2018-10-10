package ru.scorpio92.vkmd2.data.entity;


public class VkTrack {

    private String trackId;
    private String userId;
    private String artist;
    private String name;
    private int duration;
    private String urlAudio;
    private String urlImage;

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setUrlAudio(String urlAudio) {
        this.urlAudio = urlAudio;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getUserId() {
        return userId;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getUrlAudio() {
        return urlAudio;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
