package ru.scorpio92.vkmd2.presentation.base;

import android.support.annotation.Nullable;

/**
 * Слушатель действий из фрагментов в активности
 */
public interface IFragmentListener {

    /**
     * @param resultCode код результата
     * @param data данные которые может передать фрагмент в активность
     */
    void onFragmentResult(int resultCode, @Nullable Object data);
}
