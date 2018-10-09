package ru.scorpio92.vkmd2.domain.base;

import io.reactivex.observers.DisposableObserver;

public abstract class SimpleDisposableObserver<T> extends DisposableObserver<T> {

    @Override
    protected void onStart() {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onComplete() {

    }
}
