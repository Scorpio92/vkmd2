package ru.scorpio92.vkmd2.data.repository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;

public class TrackRepository implements ITrackDataSource {

    private ITrackDataSource.Remote remote;
    private ITrackDataSource.LocalDB localDB;
    private ITrackDataSource.LocalInMemory localInMemory;

    public TrackRepository(Remote remote, LocalDB localDB, LocalInMemory localInMemory) {
        this.remote = remote;
        this.localDB = localDB;
        this.localInMemory = localInMemory;
    }

    @Override
    public Flowable<List<Track>> getTrackList(int count) {
        final int[] currentRequestNum = {0};
        final int requestCount = count / GET_AUDIO_OFFSET + (count % GET_AUDIO_OFFSET > 0 ? 1 : 0);


        return localDB.getSavedTrackList().flatMap(savedTracks -> //получаем все сохранённые записи
                //получаем последовательно по N аудиозаписей
                remote.getAccountAudio(currentRequestNum[0] * GET_AUDIO_OFFSET)
                        //бегаем по полученному списку
                        .flatMap(tracks -> Observable.fromIterable(tracks)
                                .map(track -> {
                                    try {
                                        //если данная аудиозапись закэширована, отдаём её
                                        return savedTracks.get(savedTracks.indexOf(track));
                                    } catch (Exception e) {
                                        Logger.error(String.format("Track with id %s no exists in cache. Skip.", track.getTrackId()));
                                    }
                                    //если нет, отдаём полученную из сети
                                    return track;
                                })
                                .toList())
                        .flatMap(tracks -> localInMemory.addTrackList(tracks) //сохраняем в память
                                //увеличиваем номер запроса
                                .andThen(Completable.fromAction(() -> currentRequestNum[0]++))
                                .andThen(Single.just(tracks)))
                        //повторяем запрос с новым сдвигом
                        .repeat(requestCount));
    }

    @Override
    public Completable clean() {
        return null;
    }
}
