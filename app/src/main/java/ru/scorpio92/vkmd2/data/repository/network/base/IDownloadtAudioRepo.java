package ru.scorpio92.vkmd2.data.repository.network.base;


import io.reactivex.Observable;

public interface IDownloadtAudioRepo {

    Observable<Integer> downloadTrack();
}
