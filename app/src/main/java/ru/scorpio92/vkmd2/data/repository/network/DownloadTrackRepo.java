package ru.scorpio92.vkmd2.data.repository.network;

import ru.scorpio92.vkmd2.data.repository.network.core.INetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.NetworkRepository;
import ru.scorpio92.vkmd2.data.repository.network.core.PercentNetworkCallback;
import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;


public class DownloadTrackRepo extends NetworkRepository implements INetworkRepository {

    private Callback callback;

    public DownloadTrackRepo(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void execute(RequestSpecification requestSpecification) {
        makeRequest(requestSpecification, new PercentNetworkCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                if(callback != null)
                    callback.onProgressUpdate(progress);
            }

            @Override
            public void onComplete(byte[] bytes) {
                if(callback != null)
                    callback.onComplete(bytes);
            }

            @Override
            public void onError(Exception e) {
                if(callback != null)
                    callback.onError(e);
            }
        });
    }

    @Override
    public void cancel() {
        callback = null;
        super.cancel();
    }

    public interface Callback {
        void onProgressUpdate(int progress);

        void onComplete(byte[] bytes);

        void onError(Exception e);
    }
}
