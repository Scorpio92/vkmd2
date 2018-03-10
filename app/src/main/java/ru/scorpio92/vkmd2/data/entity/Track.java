package ru.scorpio92.vkmd2.data.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "MusicTable",
        indices = {@Index(value = {"trackId"}, unique = true)}
)
public class Track extends BaseTrack {

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
}
