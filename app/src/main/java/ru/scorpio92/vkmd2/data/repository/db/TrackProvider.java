package ru.scorpio92.vkmd2.data.repository.db;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private boolean randomEnabled;
    private boolean circularPlaying;


    public TrackProvider(PROVIDER provider, boolean circularPlaying) {
        Logger.log("TrackProvider", provider.name());
        this.provider = provider;
        this.circularPlaying = circularPlaying;
        this.appDatabase = AppDatabase.getInstance();
    }


    @Nullable
    @Override
    public Track getTrackByTrackId(String trackId) {
        Track track = null;
        try {
            switch (provider) {
                case SAVED_TABLE:
                    track = VkmdUtils.convertCachedTrackToBase(appDatabase.cacheDAO().getTrackByTrackId(trackId));
                    break;
                case ACCOUNT_TABLE:
                    track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByTrackId(trackId));
                    break;
                case ONLINE_SEARCH_TABLE:
                    track = getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByTrackId(trackId)));
                    break;
                case OFFLINE_SEARCH_TABLE:
                    Integer pos = appDatabase.offlineSearchDAO().getOfflineSearchItemByTrackId(trackId).getId();
                    track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByTrackId(trackId));
                    track.setId(pos);
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        return track;
    }

    @Nullable
    @Override
    public Track getPreviousTrack(Track currentTrack) {
        Track track = null;
        try {
            int currentTrackPosition = currentTrack.getId() - 1;
            switch (provider) {
                case SAVED_TABLE:
                    CachedTrack cachedTrack = AppDatabase.getInstance().cacheDAO().getPrevious(currentTrack.getId());
                    if (cachedTrack != null)
                        track = VkmdUtils.convertCachedTrackToBase(cachedTrack);
                    break;
                case ACCOUNT_TABLE:
                    track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByPosition(currentTrackPosition));
                    break;
                case ONLINE_SEARCH_TABLE:
                    track = getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByPosition(currentTrackPosition)));
                    break;
                case OFFLINE_SEARCH_TABLE:
                    String trackId = appDatabase.offlineSearchDAO().getOfflineSearchItemById(currentTrackPosition).getTrackId();
                    track = appDatabase.trackDAO().getTrackByTrackId(trackId);
                    track.setId(currentTrackPosition);
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        return track;
    }

    @Nullable
    @Override
    public Track getNextTrack(Track currentTrack) {
        Track track = null;
        try {
            int currentTrackPosition = currentTrack.getId() + 1;
            switch (provider) {
                case SAVED_TABLE:
                    CachedTrack cachedTrack;
                    if (randomEnabled) {
                        cachedTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(getRandomTrackId());
                    } else {
                        cachedTrack = AppDatabase.getInstance().cacheDAO().getNext(currentTrack.getId());
                        if(cachedTrack == null && circularPlaying) {
                            int rowId = AppDatabase.getInstance().cacheDAO().getFirstRowId();
                            cachedTrack = AppDatabase.getInstance().cacheDAO().getById(rowId);
                        }
                    }
                    if (cachedTrack != null)
                        track = VkmdUtils.convertCachedTrackToBase(cachedTrack);
                    break;
                case ACCOUNT_TABLE:
                    if (randomEnabled) {
                        track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByTrackId(getRandomTrackId()));
                    } else {
                        track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByPosition(currentTrackPosition));
                        if(track == null && circularPlaying)
                            track = getTrackWithSaveInfo(appDatabase.trackDAO().getTrackByPosition(1));
                    }
                    break;
                case ONLINE_SEARCH_TABLE:
                    if (randomEnabled) {
                        track = getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByTrackId(getRandomTrackId())));
                    } else {
                        try {
                            track = getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByPosition(currentTrackPosition)));
                        } catch (Exception e) {
                            if (circularPlaying)
                                track = getTrackWithSaveInfo(VkmdUtils.convertOnlineTrackToBase(appDatabase.onlineTrackDAO().getTrackByPosition(1)));
                            else
                                throw new Exception("end online search playlist");
                        }
                    }
                    break;
                case OFFLINE_SEARCH_TABLE:
                    String trackId = null;
                    if (randomEnabled) {
                        trackId = getRandomTrackId();
                    } else {
                        try {
                            trackId = appDatabase.offlineSearchDAO().getOfflineSearchItemById(currentTrackPosition).getTrackId();
                        } catch (Exception e) {
                            if (circularPlaying)
                                trackId = appDatabase.offlineSearchDAO().getOfflineSearchItemById(1).getTrackId();
                            else
                                throw new Exception("end offline search playlist");
                        }
                    }
                    track = appDatabase.trackDAO().getTrackByTrackId(trackId);
                    track.setId(currentTrackPosition);
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        return track;
    }

    @Override
    public void setRandomEnabled(boolean randomEnabled) {
        this.randomEnabled = randomEnabled;
    }

    private String getRandomTrackId() {
        Random random = new Random();
        List<String> trackIdList = new ArrayList<>();
        switch (provider) {
            case SAVED_TABLE:
                trackIdList.addAll(AppDatabase.getInstance().cacheDAO().getTrackIdList());
                break;
            case ACCOUNT_TABLE:
                trackIdList.addAll(AppDatabase.getInstance().trackDAO().getTrackIdList());
                break;
            case ONLINE_SEARCH_TABLE:
                trackIdList.addAll(AppDatabase.getInstance().onlineTrackDAO().getTrackIdList());
                break;
            case OFFLINE_SEARCH_TABLE:
                trackIdList.addAll(AppDatabase.getInstance().offlineSearchDAO().getTrackIdList());
                break;
        }
        int randomPosition = random.nextInt(trackIdList.size());
        return trackIdList.get(randomPosition);
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
