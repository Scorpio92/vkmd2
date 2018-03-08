package ru.scorpio92.vkmd2.data.repository.network;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.NetworkCallback;
import ru.scorpio92.vkmd2.data.repository.network.core.NetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;
import ru.scorpio92.vkmd2.tools.VkmdUtils;


public class GetAccountTrackListRepo extends NetworkRepository implements INetworkRepository {

    public interface Callback {
        void onGetTrackList(List<Track> trackList);

        void onError(Exception e);
    }

    private Callback callback;

    public GetAccountTrackListRepo(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void execute(RequestSpecification requestSpecification) {
        makeRequest(requestSpecification, new NetworkCallback() {

            @Override
            public void onComplete(byte[] bytes) {
                if (callback != null) {
                    try {
                        callback.onGetTrackList(VkmdUtils.getTrackListFromPageCode(new String(bytes, "UTF-8")));
                    } catch (UnsupportedEncodingException e) {
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
