package ru.scorpio92.vkmd2.domain.usecase.base;


import io.reactivex.observers.DisposableObserver;

public interface IRxAbstractUsecase<T> {

    void execute(DisposableObserver<T> observer);

    void cancel();
}
