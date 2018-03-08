package ru.scorpio92.vkmd2.data.repository.network;

import java.util.List;

import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.NetworkCallback;
import ru.scorpio92.vkmd2.data.repository.network.core.NetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;
import ru.scorpio92.vkmd2.tools.VkmdUtils;

/**
 * Запрос на получение списка аудиозаписей (онлайн поиск)
 */
public class GetSearchTrackListRepo extends NetworkRepository implements INetworkRepository {

    public interface Callback {
        void onGetTrackList(List<OnlineTrack> trackList);

        void onError(Exception e);
    }

    private String uid;
    private Callback callback;

    public GetSearchTrackListRepo(String uid, Callback callback) {
        this.uid = uid;
        this.callback = callback;
    }

    @Override
    public void execute(RequestSpecification requestSpecification) {
        makeRequest(requestSpecification, new NetworkCallback() {

            @Override
            public void onComplete(byte[] bytes) {
                if (callback != null) {
                    try {
                        callback.onGetTrackList(VkmdUtils.getTrackListFromSearchPageCode(uid, new String(bytes, "UTF-8")));
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                if (callback != null)
                    callback.onError(e);
            }
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        callback = null;
    }
}
