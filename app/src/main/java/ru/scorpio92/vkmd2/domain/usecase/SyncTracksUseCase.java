package ru.scorpio92.vkmd2.domain.usecase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.domain.datasource.ISyncDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.domain.entity.exception.BadTrackCountException;

import static ru.scorpio92.vkmd2.domain.base.ValidateUtils.throwIfNull;


/**
 *  Синхронизация списка аудиозаписей
 */
public class SyncTracksUseCase extends AbstractUseCase<List<Track>> {

    private int syncCount;
    private ISyncDataSource syncDataSource;
    private ITrackDataSource trackRepository;

    public SyncTracksUseCase(ISyncDataSource syncDataSource, ITrackDataSource trackRepository) {
        this.syncDataSource = syncDataSource;
        this.trackRepository = trackRepository;
    }

    @Override
    protected void checkPreconditions(DisposableObserver<List<Track>> observer) throws Exception {
        if(syncCount < 1)
            throw new BadTrackCountException();
        throwIfNull(syncDataSource);
        throwIfNull(trackRepository);
    }

    @Override
    public Observable<List<Track>> provideObservable() throws Exception {
        return trackRepository.getTrackList(syncCount)
                .toObservable()
                .doOnComplete(() -> syncDataSource.saveSyncCount(syncCount).blockingAwait());
    }

    public void execute(int syncCount, DisposableObserver<List<Track>> observer) {
        this.syncCount = syncCount;
        execute(observer);
    }

    /*private static final String LOG_TAG = GetAccountTracksUseCase.class.getSimpleName();

    private String cookie;
    *//**
     * требуемое кол-во аудиозаписей
     *//*
    private int count;

    public GetAccountTracksUseCase(String cookie, int count) {
        this.cookie = cookie;
        this.count = count;
    }

    @Override
    public Observable<String> provideObservable() {
        return buildChain()
                .doOnNext(tracks -> {
                    AppDatabase.getInstance().trackDAO().deleteAll();
                    AppDatabase.getInstance().trackDAO().addTrackList(tracks);
                })
                .flatMap(tracks -> Observable.just(tracks.get(0).getUserId()))
                .subscribeOn(provideSubscribeScheduler());
    }

    private Observable<List<Track>> buildChain() {
        int requestCount = count / GET_AUDIO_OFFSET + (count % GET_AUDIO_OFFSET > 0 ? 1 : 0);
        Logger.log(LOG_TAG, "requestCount: " + requestCount);

        GetAudioRepo getAudioRepo = new GetAudioRepo(cookie);
        List<Observable<List<Track>>> observables = new ArrayList<>();

        int currentOffset = 0;
        for (int i = 0; i < requestCount; i++) {
            Logger.log(LOG_TAG, "currentOffset: " + currentOffset);
            observables.add(getAudioRepo.getAccountAudio(currentOffset));
            currentOffset += GET_AUDIO_OFFSET;
        }

        //собираем все запросы и ждем пока они синхронно отработают
        return Observable.zip(observables, objects -> {
            int idx = 1;
            List<Track> generalTrackList = new ArrayList<>();
            for (Object o : objects) {
                List<Track> trackList = (List<Track>) o;
                for (Track track : trackList) {
                    if (!generalTrackList.contains(track)) {
                        track.setId(idx);
                        generalTrackList.add(track);
                        idx++;
                    }
                }
            }
            return generalTrackList;
        });
    }*/
}
