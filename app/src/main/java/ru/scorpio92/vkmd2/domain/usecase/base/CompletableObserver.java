package ru.scorpio92.vkmd2.domain.usecase.base;


import io.reactivex.observers.DisposableObserver;

public abstract class CompletableObserver extends DisposableObserver {

    @Override
    public void onNext(Object o) {

    }
}
