package ru.scorpio92.vkmd2.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.dao.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.network.GetAudioRepo;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;


/**
 * Получение списка аудиозаписей с аккаунта пользователя
 */
public class GetAccountTracksUseCase extends AbstractUseCase<String> {

    private static final String LOG_TAG = GetAccountTracksUseCase.class.getSimpleName();

    private String cookie;
    /**
     * требуемое кол-во аудиозаписей
     */
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
                    AppDatabase.getInstance().trackDAO().saveTrackList(tracks);
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
    }
}
