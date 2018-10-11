package ru.scorpio92.vkmd2.data.datasource.db.base;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface IDatabaseDataSource<O> {

    /**
     * Выбор имеющейся строки из таблицы
     * В кач-ве источника данных выбран именно SingleSource, т.к. мы выбираем конкретную запись
     * т.е. она либо есть, либо в случае если ее нет будет брошена ошибка
     * по-умолчанию не вернет ничего
     * @param value идентификатор по которому выбираем запись
     */
    Single<O> select(String value);

    /**
     * Выбор массива строк
     * В кач-ве источника данных выбран именно MaybeSource, т.к. мы выбираем все записи
     * Они либо есть, либо их нет(таблица пустая)
     */
    Maybe<List<O>> selectAll();

    /**
     * Вставка списка значений в таблицу
     * Без реактивной отдачи
     */
    Completable insert(List<O> objects) throws Exception;

    /**
     * Удаление записи из таблицы
     */
    Completable delete(O object) throws Exception;
}
