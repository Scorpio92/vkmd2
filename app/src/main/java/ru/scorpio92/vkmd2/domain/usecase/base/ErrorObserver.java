package ru.scorpio92.vkmd2.domain.usecase.base;


import io.reactivex.observers.DisposableObserver;

public abstract class ErrorObserver extends DisposableObserver {

    @Override
    public void onNext(Object o) {

    }

    @Override
    public void onComplete() {

    }
}
