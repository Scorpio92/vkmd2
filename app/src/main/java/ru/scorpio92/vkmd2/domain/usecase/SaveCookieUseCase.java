package ru.scorpio92.vkmd2.domain.usecase;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;

public class SaveCookieUseCase extends AbstractUseCase {

    private ICookieDataSource cookieDataSource;
    private String cookie;

    public SaveCookieUseCase(ICookieDataSource cookieDataSource) {
        this.cookieDataSource = cookieDataSource;
    }

    @Override
    public Observable provideObservable() throws Exception {
        return cookieDataSource.saveCookie(cookie).toObservable();
    }

    public void execute(String cookie, DisposableObserver disposableObserver) {
        this.cookie = cookie;
        execute(disposableObserver);
    }
}
