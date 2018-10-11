package ru.scorpio92.vkmd2.domain.base;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Класс для утилит-валидаторов
 */
public class ValidateUtils {

    /**
     * Проверка строкового параметра на null и empty
     * @param param строка для валидации
     * @return true если не null и не пустой
     */
    public static boolean validateParam(String param) {
        return param != null && !param.isEmpty();
    }

    /**
     * Бросаем ошибку если параметр null
     */
    public static <O> O throwIfNull(@Nullable O o) {
        if(o == null)
            throw new NullPointerException("parameter is null");

        return o;
    }
}
