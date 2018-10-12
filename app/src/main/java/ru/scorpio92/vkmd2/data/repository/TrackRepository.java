package ru.scorpio92.vkmd2.data.repository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ITrackDataSource;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;

public class TrackRepository implements ITrackDataSource {

    private ICookieDataSource mCookieDataSource;
    private ITrackDataSource.Remote mRemoteDataSource;
    private ITrackDataSource.LocalDB mLocalDbDataSource;
    private ITrackDataSource.LocalInMemory localInMemoryDataSource;

    private int mCurrentRequestNum;
    private List<Track> mCacheBackup;

    public TrackRepository(ICookieDataSource cookieDataSource, Remote remote, LocalDB localDB,
                           LocalInMemory localInMemory) {
        this.mCookieDataSource = cookieDataSource;
        this.mRemoteDataSource = remote;
        this.mLocalDbDataSource = localDB;
        this.localInMemoryDataSource = localInMemory;
    }

    @Override
    public Flowable<List<Track>> getCachedTrackList() {
        return localInMemoryDataSource.getTrackList();
    }

    @Override
    public Flowable<List<Track>> getTrackList(int count) {
        Logger.log("getTrackList: " + count);
        mCurrentRequestNum = 0;
        mCacheBackup = new ArrayList<>();

        return backupLocalCacheAndClean(getSavedTracksAndStartGetFromRemote(count))
                .onErrorResumeNext(this::onError)
                .doOnComplete(mCacheBackup::clear); //чистим массив с бэкапом
    }

    @Override
    public Completable clean() {
        return null;
    }

    /**
     * делаем бэкап текущих треков в кэше, затем чистим его и продолжаем цепочку дальше
     *
     * @param nextSource следующий источник данных
     */
    private Flowable<List<Track>> backupLocalCacheAndClean(Flowable<List<Track>> nextSource) {
        return localInMemoryDataSource.getTrackList().flatMap(tracksFromCache -> {
            mCacheBackup.addAll(tracksFromCache);
            Logger.log("Add tracksFromCache to backup: " + mCacheBackup.size());
            return localInMemoryDataSource.clean()
                    .andThen(nextSource);
        });
    }

    /**
     * Получение списка сохранённых треков и далее получение их из сети
     */
    private Flowable<List<Track>> getSavedTracksAndStartGetFromRemote(int tracksCount) {
        return mLocalDbDataSource.getSavedTrackList().flatMap((List<Track> savedTracks) -> {
            Logger.log("Get saved tracks from DB: " + savedTracks.size());
            return TrackRepository.this.getAudioFromRemote(tracksCount, savedTracks);
        });
    }

    /**
     * Получить кол-во запросов исходя из кол-ва запрашиваемых треков
     */
    private int getRequestCount(int tracksCount) {
        return tracksCount / GET_AUDIO_OFFSET + (tracksCount % GET_AUDIO_OFFSET > 0 ? 1 : 0);
    }

    /**
     * Получить список треков из сети
     */
    private Flowable<List<Track>> getAudioFromRemote(int tracksCount, List<Track> savedTracks) {
        Logger.log("Run getAudioFromRemote. Current request num: " + mCurrentRequestNum);
        return mCookieDataSource.getCookie().flatMapPublisher(cookie ->
                mRemoteDataSource.getAccountAudio(cookie, mCurrentRequestNum * GET_AUDIO_OFFSET)
                        //бегаем по полученному списку
                        .flatMap(tracks -> mergeTracksFromRemoteWithSaved(tracks, savedTracks))
                        //сохраняем в кэш
                        .flatMap(TrackRepository.this::saveTracksToCacheAndIncreaseRequestCount)
                        //повторяем запрос с новым сдвигом
                        .repeat(getRequestCount(tracksCount)));
    }

    /**
     * Обработка треков из сети и проверка есть ли среди сохранённых, треки полученные из сети
     * Если есть, отдаём сохранённые вместо тех которые пришли из сети
     */
    private Single<List<Track>> mergeTracksFromRemoteWithSaved(List<Track> remoteTracks, List<Track> savedTracks) {
        Logger.log("mergeTracksFromRemoteWithSaved");
        return Observable.fromIterable(remoteTracks)
                .map(track -> {
                        //если данная аудиозапись закэширована, отдаём её
                        int idx = savedTracks.indexOf(track);
                        if(idx > -1) {
                            return savedTracks.get(idx);
                        } else {
                            //Logger.error(String.format("Track with id %s no exists in cache. Skip.", track.getTrackId()));
                        }
                    //если нет, отдаём полученную из сети
                    return track;
                })
                .toList();
    }

    /**
     * Сохраняем готовые треки в кэш и увеличиваем номер текущего запроса на 1
     */
    private Single<List<Track>> saveTracksToCacheAndIncreaseRequestCount(List<Track> tracks) {
        return localInMemoryDataSource.addTrackList(tracks) //сохраняем в память
                //увеличиваем номер запроса
                .andThen(Completable.fromAction(() -> mCurrentRequestNum++))
                .andThen(Single.just(tracks));
    }

    /**
     * Обрабатываем ошибки
     */
    private Flowable<List<Track>> onError(Throwable throwable) {
        Logger.error((Exception) throwable);
        if (mCurrentRequestNum == 0) { //если ещё не сделали ни одного успешного запроса
            Logger.log("onErrorResumeNext. currentRequestNum == 0");
            //возвращаем всё как было
            return localInMemoryDataSource.clean() //чистим кэш
                    .andThen(localInMemoryDataSource.addTrackList(mCacheBackup)) //восстанавливаем бэкап
                    .andThen(Completable.fromAction(mCacheBackup::clear)) //чистим массив с бэкапом
                    .andThen(getCachedTrackList()); //отдаём всё из кэша
        } else { //если уже успели сделать какие-то запросы, возвращаем пустой массив
            Logger.log("onErrorResumeNext. currentRequestNum != 0");
            return Completable.fromAction(mCacheBackup::clear) //чистим массив с бэкапом
                    .andThen(Flowable.empty()); //отдаём событие onComplete
        }
    }
}
