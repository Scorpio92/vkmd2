package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Провайдер настроек
 */
public interface IMpSettingsProvider {

    /**
     * Сохранить список настроек
     */
    void saveSettings(MpSettings mpSettings) throws Exception;

    /**
     * Получить список текущих настроек
     */
    MpSettings loadSettings() throws Exception;
}
