package ru.scorpio92.vkmd2.data.repository.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;


@Dao
public interface CacheDAO {

    @Query("DELETE from DownloadTable")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveDownloadList(List<CachedTrack> trackList);

    @Query("SELECT * FROM DownloadTable WHERE status != 1")
    List<CachedTrack> getDownloadList();

    @Query("SELECT trackId FROM DownloadTable WHERE status == 0 LIMIT 1")
    String getTrackIdForDownload();

    @Query("DELETE FROM DownloadTable WHERE trackId IN (:trackIdList)")
    void removeFromDownloadList(List<String> trackIdList);

    @Query("UPDATE DownloadTable SET status = 0 WHERE trackId = :trackId")
    void markFileAsNotDownloaded(String trackId);

    @Query("UPDATE DownloadTable SET status = 1, savedPath = :savedPath WHERE trackId = :trackId")
    void markFileAsDownloaded(String trackId, String savedPath);

    @Query("UPDATE DownloadTable SET status = 2 WHERE trackId = :trackId")
    void markFileAsError(String trackId);

    @Query("SELECT * FROM DownloadTable WHERE status == 1")
    List<CachedTrack> getSavedTracks();

    @Query("SELECT COUNT(*) FROM DownloadTable WHERE status == 1")
    int getSavedTracksCount();

    @Query("SELECT * FROM DownloadTable WHERE trackId == :trackId")
    CachedTrack getTrackByTrackId(String trackId);

    @Query("SELECT id FROM DownloadTable ORDER BY id DESC LIMIT 1")
    int getLastRowId();

    @Query("SELECT * FROM DownloadTable WHERE id < :id ORDER BY id DESC LIMIT 1")
    CachedTrack getPrevious(int id);

    @Query("SELECT * FROM DownloadTable WHERE id > :id ORDER BY id LIMIT 1")
    CachedTrack getNext(int id);

    @Query("SELECT trackId FROM DownloadTable")
    List<String> getTrackIdList();
}
