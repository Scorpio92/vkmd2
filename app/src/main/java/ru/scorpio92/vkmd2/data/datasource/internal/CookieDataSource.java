package ru.scorpio92.vkmd2.data.datasource.internal;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.scorpio92.vkmd2.data.datasource.internal.base.AbstractLocalDataSource;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;

public class CookieDataSource extends AbstractLocalDataSource implements ICookieDataSource {

    @Override
    public Single<Boolean> checkCookieExists() {
        return Single.fromCallable(this::checkStoreExists);
    }

    @Override
    public Completable saveCookie(String cookie) {
        return Completable.fromAction(() -> saveData(cookie));
    }

    @Override
    public Single<String> getCookie() {
        return Single.fromCallable(this::getData);
    }

    @Override
    protected String provideStoreName() {
        return ".cookie";
    }

    @Override
    protected boolean enableEncryption() {
        return false;
    }
}
