package ru.scorpio92.vkmd2.domain.base;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Базовый класс, описывающий единичное действие
 *
 * @param <T> тип возвращаемого объекта
 */
public abstract class AbstractUseCase<T> {

    private DisposableObserver<T> mObserver;

    /**
     * Шедулер для потока выполнения по-умолчанию
     * В случае необходимости его можно переопределить
     */
    protected Scheduler provideSubscribeScheduler() {
        return Schedulers.io();
    }

    /**
     * Метод, в котором описывается бизнес-логика
     * Этот метод можно "дёргать" из других UseCase, объединяя их в цепочки
     */
    public abstract Observable<T> provideObservable() throws Exception;

    /**
     * Проверка предварительных условий (валидация и т.д.). Переопределить при необходимости
     *
     * @param observer гарантированно не null
     * @throws Exception в данном блоке не отлавливаем исключения
     */
    protected void checkPreconditions(DisposableObserver<T> observer) throws Exception {

    }

    /**
     * Метод запуска бизнес-логики
     */
    public void execute(DisposableObserver<T> observer) {
        if(observer == null)
            throw new IllegalArgumentException("Observer is null");

        try {
            checkPreconditions(observer);
            this.mObserver = provideObservable()
                    .subscribeOn(provideSubscribeScheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer);
        } catch (Exception e) {
            if (!observer.isDisposed())
                observer.onError(e);
        }
    }

    /**
     * Остановка бизнес-логики
     * Данный метод следует вызывать при отписке (например, в презентере, в методе onDestroy())
     */
    public void cancel() {
        if (mObserver != null && !mObserver.isDisposed()) {
            mObserver.dispose();
        }
    }
}
