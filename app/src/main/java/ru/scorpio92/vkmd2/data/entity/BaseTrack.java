package ru.scorpio92.vkmd2.data.entity;

import android.arch.persistence.room.PrimaryKey;


public abstract class BaseTrack {

    @PrimaryKey
    private int id;
    private String userId;
    private String trackId;
    private String artist;
    private String name;
    private int duration;
    private String urlAudio;
    private String urlImage;


    public void setId(int id) {
        this.id = id;
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


    public int getId() {
        return id;
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


    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj != null && obj instanceof Track) {
            equals = this.trackId.equals(((Track) obj).getTrackId());
        }

        return equals;
    }
}
