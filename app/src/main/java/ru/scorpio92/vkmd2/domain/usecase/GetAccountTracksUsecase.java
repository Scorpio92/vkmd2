package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.GetAccountTrackListRepo;
import ru.scorpio92.vkmd2.data.repository.network.core.BadRequestException;
import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.specifications.GetAccountTrackList;
import ru.scorpio92.vkmd2.domain.threading.ThreadExecutor;
import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;
import ru.scorpio92.vkmd2.domain.usecase.base.IUsecaseBaseCallback;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.Constants.GET_AUDIO_OFFSET;


/**
 * Получение списка аудиозаписей с аккаунта пользователя
 */
public class GetAccountTracksUsecase extends AbstractUsecase {

    private static final String LOG_TAG = GetAccountTracksUsecase.class.getSimpleName();

    private String cookie;
    /**
     * требуемое кол-во аудиозаписей
     */
    private int count;
    private UsecaseCallback callback;
    private List<INetworkRepository> repositories;

    public GetAccountTracksUsecase(String cookie, int count, UsecaseCallback callback) {
        this.cookie = cookie;
        this.count = count;
        this.callback = callback;
        this.repositories = new ArrayList<>();
    }

    @Override
    protected IExecutor provideExecutor() {
        return ThreadExecutor.getInstance(true);
    }

    @Override
    public void run() {
        int requestCount = count / GET_AUDIO_OFFSET + (count % GET_AUDIO_OFFSET > 0 ? 1 : 0);
        Logger.log(LOG_TAG, "requestCount: " + requestCount);

        List<Track> generalTrackList = new ArrayList<>();

        int currentOffset = 0;
        final int[] idx = {1};
        for (int i = 0; i < requestCount; i++) {
            Logger.log(LOG_TAG, "currentOffset: " + currentOffset);

            INetworkRepository repo = new GetAccountTrackListRepo(new GetAccountTrackListRepo.Callback() {
                @Override
                public void onGetTrackList(List<Track> trackList) {
                    for (Track track : trackList) {
                        if (!generalTrackList.contains(track)) {
                            track.setId(idx[0]);
                            generalTrackList.add(track);
                            idx[0]++;
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
            repositories.add(repo);
            repo.execute(new GetAccountTrackList(cookie, currentOffset));

            currentOffset += GET_AUDIO_OFFSET;
        }

        if (callback != null) {
            if (!generalTrackList.isEmpty()) {
                runOnUI(() -> {
                    if (callback != null)
                        callback.onGetUID(generalTrackList.get(0).getUserId());
                });
                try {
                    AppDatabase.getInstance().trackDAO().deleteAll();
                    AppDatabase.getInstance().trackDAO().saveTrackList(generalTrackList);
                } catch (Exception e) {
                    runOnUI(() -> {
                        if (callback != null)
                            callback.onError(e);
                    });
                    return;
                }

                runOnUI(() -> {
                    if (callback != null)
                        callback.onComplete();
                });
            } else {
                runOnUI(() -> {
                    if (callback != null)
                        callback.onError(new BadRequestException());
                });
            }
        }
    }

    @Override
    protected void onInterrupt() {
        for (INetworkRepository repository : repositories)
            repository.cancel();
        callback = null;
    }

    public interface UsecaseCallback extends IUsecaseBaseCallback {
        void onGetUID(String uid);

        void onComplete();
    }
}
