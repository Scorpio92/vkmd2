package ru.scorpio92.vkmd2.data.repository.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;


@Dao
public interface TrackDAO {

    @Query("DELETE from VkTrack")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTrackList(List<Track> trackList);

    @Query("SELECT * FROM VkTrack")
    List<Track> getTrackList();

    @Query("SELECT COUNT(*) FROM VkTrack")
    int getTracksCount();

    @Query("SELECT * FROM VkTrack WHERE trackId == :trackId")
    Track getTrackByTrackId(String trackId);

    @Query("SELECT * FROM VkTrack WHERE id == :position")
    Track getTrackByPosition(int position);

    @Query("SELECT trackId FROM VkTrack")
    List<String> getTrackIdList();
}
