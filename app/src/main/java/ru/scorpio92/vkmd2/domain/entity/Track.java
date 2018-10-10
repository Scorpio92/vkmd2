package ru.scorpio92.vkmd2.domain.entity;

public class Track {

    public static final int TRACK_NOT_DOWNLOADED = 0;
    public static final int TRACK_DOWNLOADED = 1;
    public static final int TRACK_DOWNLOAD_ERROR = 2;

    private String userId;
    private String trackId;
    private String artist;
    private String name;
    private int duration;
    private String urlAudio;
    private String urlImage;

    private int status;
    private String savedPath;


    public Track() {
        this.status = TRACK_NOT_DOWNLOADED;
        this.savedPath = "";
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
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

    public String getUserId() {
        return userId;
    }

    public String getTrackId() {
        return trackId;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }


    public int getStatus() {
        return status;
    }

    public String getSavedPath() {
        return savedPath;
    }


    public boolean isSaved() {
        return status == TRACK_DOWNLOADED;
    }
}
