package ru.scorpio92.vkmd2.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;


@Entity(
        tableName = "DownloadTable",
        indices = {@Index(value = {"trackId"}, unique = true)}
)
public class CachedTrack extends BaseTrack {

    public static final int TRACK_NOT_DOWNLOADED = 0;
    public static final int TRACK_DOWNLOADED = 1;
    public static final int TRACK_DOWNLOAD_ERROR = 2;

    private int status;
    private String savedPath;


    public CachedTrack() {
        this.status = TRACK_NOT_DOWNLOADED;
        this.savedPath = "";
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
