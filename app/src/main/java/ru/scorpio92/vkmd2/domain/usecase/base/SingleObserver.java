package ru.scorpio92.vkmd2.domain.usecase.base;


import io.reactivex.observers.DisposableObserver;

public abstract class SingleObserver<T> extends DisposableObserver<T> {

    @Override
    public void onComplete() {

    }
}
