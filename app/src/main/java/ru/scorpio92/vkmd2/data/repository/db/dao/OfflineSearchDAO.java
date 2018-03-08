package ru.scorpio92.vkmd2.data.repository.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.OfflineSearchItem;

@Dao
public interface OfflineSearchDAO {

    @Query("DELETE from OfflineSearchTable")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveTrackIdList(List<OfflineSearchItem> offlineSearchItems);

    @Query("SELECT * FROM OfflineSearchTable")
    List<OfflineSearchItem> getTrackList();

    @Query("SELECT COUNT(*) FROM OfflineSearchTable")
    int getTracksCount();

    @Query("SELECT * FROM OfflineSearchTable WHERE trackId == :trackId")
    OfflineSearchItem getOfflineSearchItemByTrackId(String trackId);

    @Query("SELECT * FROM OfflineSearchTable WHERE id == :id")
    OfflineSearchItem getOfflineSearchItemById(int id);
}