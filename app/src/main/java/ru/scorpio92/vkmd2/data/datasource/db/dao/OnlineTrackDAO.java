package ru.scorpio92.vkmd2.data.datasource.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;

@Dao
public interface OnlineTrackDAO {

    @Query("DELETE from OnlineSearchTable")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTrackList(List<OnlineTrack> trackList);

    @Query("SELECT * FROM OnlineSearchTable")
    List<Track> getTrackList();

    @Query("SELECT COUNT(*) FROM OnlineSearchTable")
    int getTracksCount();

    @Query("SELECT * FROM OnlineSearchTable WHERE trackId == :trackId")
    OnlineTrack getTrackByTrackId(String trackId);

    @Query("SELECT * FROM OnlineSearchTable WHERE id == :position")
    OnlineTrack getTrackByPosition(int position);

    @Query("SELECT trackId FROM OnlineSearchTable")
    List<String> getTrackIdList();
}
