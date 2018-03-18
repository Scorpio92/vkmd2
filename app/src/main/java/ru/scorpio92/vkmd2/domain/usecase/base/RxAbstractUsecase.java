package ru.scorpio92.vkmd2.domain.usecase.base;


import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class RxAbstractUsecase<T> implements IRxAbstractUsecase<T> {

    private CompositeDisposable compositeDisposable;

    protected abstract Observable<T> provideObservable();

    protected Scheduler provideSubscribeScheduler() {
        return Schedulers.io();
    }

    @Override
    public void execute(DisposableObserver<T> observer) {
        Observable<T> observable = provideObservable();
        if (observable == null) {
            if (observer != null)
                observer.onError(new IllegalArgumentException());
            return;
        }
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));
    }

    @Override
    public void cancel() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
