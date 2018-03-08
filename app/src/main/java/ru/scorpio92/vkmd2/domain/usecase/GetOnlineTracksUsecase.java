package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.CachedTrack;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.GetSearchTrackListRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.specifications.GetSearchTrackList;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


/**
 * Поиск аудиозаписей онлайн
 */
public class GetOnlineTracksUsecase extends AbstractUsecase {

    /**
     * id пользователя под которым выполняется поиск
     */
    private String uid;
    private String cookie;
    /**
     * ключевая фраза (поисковый запрос)
     */
    private String searchQuery;
    private UsecaseCallback callback;
    private INetworkRepository repo;

    public GetOnlineTracksUsecase(String uid, String cookie, String searchQuery, UsecaseCallback callback) {
        this.uid = uid;
        this.cookie = cookie;
        this.searchQuery = searchQuery;
        this.callback = callback;
    }

    @Override
    protected IExecutor provideExecutor() {
        return ThreadExecutor.getInstance(false);
    }

    @Override
    public void run() {
        if (repo != null)
            repo.cancel();

        repo = new GetSearchTrackListRepo(uid, new GetSearchTrackListRepo.Callback() {
            @Override
            public void onGetTrackList(List<OnlineTrack> onlineTrackList) {
                if (callback != null) {
                    try {
                        AppDatabase.getInstance().onlineTrackDAO().saveTrackList(onlineTrackList);

                        List<Track> trackList = new ArrayList<>();

                        for (OnlineTrack onlineTrack : onlineTrackList) {
                            CachedTrack cachedTrack = AppDatabase.getInstance().cacheDAO().getTrackByTrackId(onlineTrack.getTrackId());
                            Track track = VkmdUtils.convertOnlineTrackToBase(onlineTrack);
                            if (cachedTrack != null) {
                                track.setSaved(cachedTrack.isSaved());
                                track.setSavedPath(cachedTrack.getSavedPath());
                            }
                            trackList.add(track);
                        }

                        runOnUI(() -> {
                            if (callback != null)
                                callback.onComplete(trackList);
                        });
                    } catch (Exception e) {
                        runOnUI(() -> {
                            if (callback != null)
                                callback.onError(e);
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUI(() -> {
                    if (callback != null)
                        callback.onError(e);
                });
            }
        });
        repo.execute(new GetSearchTrackList(cookie, searchQuery));
    }

    @Override
    protected void onInterrupt() {
        callback = null;
        if (repo != null)
            repo.cancel();
    }

    public interface UsecaseCallback extends IUsecaseBaseCallback {
        void onComplete(List<Track> trackList);
    }
}
