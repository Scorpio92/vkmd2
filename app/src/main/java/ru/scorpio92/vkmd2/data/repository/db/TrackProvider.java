package ru.scorpio92.vkmd2.data.repository.db;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.db.base.ITrackProvider;
import ru.scorpio92.vkmd2.tools.Logger;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Выбор источника воспроизведения
 */
public class TrackProvider implements ITrackProvider {

    public enum PROVIDER {
        SAVED_TABLE,
        ACCOUNT_TABLE,
        ONLINE_SEARCH_TABLE,
        OFFLINE_SEARCH_TABLE
    }

    private PROVIDER provider;
    private AppDatabase appDatabase;
    private List<Track> savedTracksCache;

    public TrackProvider(PROVIDER provider) {
        Logger.log("TrackProvider", provider.name());
        this.provider = provider;
        this.appDatabase = AppDatabase.getInstance();
        this.savedTracksCache = new ArrayList<>();

        if (provider == PROVIDER.SAVED_TABLE) {
            try {
                List<CachedTrack> cachedTrackList = appDatabase.cacheDAO().getSavedTracks();
                for (CachedTrack cachedTrack : cachedTrackList)
                    savedTracksCache.add(VkmdUtils.convertCachedTrackToBase(cachedTrack));
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    @Override
    public int getTracksCount() {
        switch (provider) {
            case SAVED_TABLE:
                return appDatabase.cacheDAO().getSavedTracksCount();
            case ACCOUNT_TABLE:
                return appDatabase.trackDAO().getTracksCount();
            case ONLINE_SEARCH_TABLE:
                return appDatabase.onlineTrackDAO().getTracksCount();
            case OFFLINE_SEARCH_TABLE:
                return appDatabase.offlineSearchDAO().getTracksCount();
            default:
                return 0;
        }
    }

    @Nullable
    @Override
    public Track getTrackByTrackId(String trackId) {
        try {
            switch (provider) {
                case SAVED_TABLE:
                    return VkmdUtils.convertCachedTrackToBase(appDatabase.cacheDAO().getTrackByTrackId(trackId));
                case ACCOUNT_TABLE:
                    return getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByTrackId(trackId));
                case ONLINE_SEARCH_TABLE:
                    return getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByTrackId(trackId)));
                case OFFLINE_SEARCH_TABLE:
                    Integer pos = appDatabase.offlineSearchDAO().getOfflineSearchItemByTrackId(trackId).getId();
                    Track track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByTrackId(trackId));
                    track.setId(pos);
                    return track;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }


    @Nullable
    @Override
    public Track getTrackByPosition(int position) {
        try {
            switch (provider) {
                case SAVED_TABLE:
                    return savedTracksCache.get(position - 1);
                case ACCOUNT_TABLE:
                    return getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByPosition(position));
                case ONLINE_SEARCH_TABLE:
                    return getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByPosition(position)));
                case OFFLINE_SEARCH_TABLE:
                    String trackId = appDatabase.offlineSearchDAO().getOfflineSearchItemById(position).getTrackId();
                    Track track = appDatabase.trackDAO().getTrackByTrackId(trackId);
                    track.setId(position);
                    return track;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return null;
    }

    private Track getTrackWithSaveInfo(Track track) {
        if (track != null) {
            CachedTrack cachedTrack = appDatabase.cacheDAO().getTrackByTrackId(track.getTrackId());
            if (cachedTrack != null) {
                track.setSaved(cachedTrack.isSaved());
                track.setSavedPath(cachedTrack.getSavedPath());
            }
        }
        return track;
    }
}
