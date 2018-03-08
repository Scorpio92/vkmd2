package ru.scorpio92.vkmd2.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(
        tableName = "OfflineSearchTable",
        indices = {@Index(value = {"trackId"}, unique = true)}
)
public class OfflineSearchItem {

    @PrimaryKey
    private int id;
    private String trackId;

    public OfflineSearchItem(int id, String trackId) {
        this.id = id;
        this.trackId = trackId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTrackId() {
        return trackId;
    }
}
