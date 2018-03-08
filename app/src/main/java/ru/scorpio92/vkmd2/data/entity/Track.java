package ru.scorpio92.vkmd2.data.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "MusicTable",
        indices = {@Index(value = {"trackId"}, unique = true)}
)
public class Track {

    @PrimaryKey
    private int id;
    private String userId;
    private String trackId;
    private String artist;
    private String name;
    private int duration;
    private String urlAudio;
    private String urlImage;

    @Ignore
    private boolean saved;
    @Ignore
    private String savedPath;
    @Ignore
    private int downloadProgress;
    @Ignore
    private boolean downloadError;
    @Ignore
    private boolean isPlaying;


    public Track() {
        this.saved = false;
        this.savedPath = "";
    }


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

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public void setDownloadError(boolean downloadError) {
        this.downloadError = downloadError;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
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

    public boolean isSaved() {
        return saved;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public boolean isDownloadError() {
        return downloadError;
    }

    public boolean isPlaying() {
        return isPlaying;
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
