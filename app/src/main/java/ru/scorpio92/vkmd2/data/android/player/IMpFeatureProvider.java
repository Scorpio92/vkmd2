package ru.scorpio92.vkmd2.data.android.player;

import java.util.List;

/**
 * Провайдер настроек
 */
public interface IMpFeatureProvider {

    /**
     * Получить список текущих настроек
     */
    List<MpFeature> loadFeatures() throws Exception;

    /**
     * Изменить настройку
     */
    void changeFeature(MpFeature feature) throws Exception;
}
