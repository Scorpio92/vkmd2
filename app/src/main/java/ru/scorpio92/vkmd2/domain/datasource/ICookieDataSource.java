package ru.scorpio92.vkmd2.domain.datasource;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface ICookieDataSource {

    Single<Boolean> checkCookieExists();

    Completable saveCookie(String cookie);

    Single<String> getCookie();
}
