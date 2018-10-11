package ru.scorpio92.vkmd2.data.datasource.db.base;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Базовый класс источника данных для работы с БД
 * Заточен для работы с Google Room
 */
public abstract class AbstractDatabaseDataSource<D extends RoomDatabase, O> implements IDatabaseDataSource<O> {

    private static volatile RoomDatabase sDatabase;

    /**
     * Инициализация глобального инстанса БД
     */
    public static synchronized void initDatabase(@NonNull Context context, Class appDatabase, @NonNull String dbName) {
        if (sDatabase == null)
            sDatabase = Room.databaseBuilder(context.getApplicationContext(), appDatabase, dbName).build();
    }

    /**
     * Закрытие глобального инстанса БД
     */
    public static synchronized void closeDatabase() {
        if (sDatabase != null) {
            sDatabase.close();
            sDatabase = null;
        }
    }

    @Override
    public Single<O> select(String value) {
        return Single.never();
    }

    @Override
    public Maybe<List<O>> selectAll() {
        return Maybe.empty();
    }

    @Override
    public Completable insert(List<O> objects) throws Exception {
        return Completable.complete();
    }

    @Override
    public Completable delete(O object) throws Exception {
        return Completable.complete();
    }

    /**
     * Получение экземпляра БД потомком
     */
    @Nullable
    protected D getDatabase() {
        return (D) sDatabase;
    }

    /**
     * Получение экземпляра БД потомком в реактивном стиле
     */
    protected Single<D> getDatabaseRx() {
        return Single.fromCallable(new Callable<D>() {
            @Override
            public D call() throws Exception {
                return (D) sDatabase;
            }
        });
    }
}
