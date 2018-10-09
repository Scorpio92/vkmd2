package ru.scorpio92.vkmd2.presentation.base;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Базовый интефрейс UI класса
 * То, что должна уметь любая вью
 */
public interface IBaseView {

    /**
     * Получить контекст вью
     * к примеру, для использования в презентере
     */
    @Nullable
    Context getViewContext();

    @Nullable
    Activity getActivity();

    /**
     * Показать прогресс
     */
    void showProgress();

    /**
     * Скрыть прогресс
     */
    void hideProgress();

    /**
     * Показать ошибку
     * @param error текст ошибки. Презентер должен гарантировать не null значение
     */
    void onError(@NonNull String error);
}