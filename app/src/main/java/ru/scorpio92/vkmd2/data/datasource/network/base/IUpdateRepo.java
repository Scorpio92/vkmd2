package ru.scorpio92.vkmd2.data.datasource.network.base;


import io.reactivex.Observable;

public interface IUpdateRepo {

    Observable<String> getLastVersion();

    Observable<String> downloadLastBuild(String version);
}
