package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Интерфейс для реализации посредника между реализацией стандартного Android MP и сервиса для
 * проигрывания музыки
 */
public interface IMediaPlayer {

    /**
     * вкл/выкл настройку
     */
    void enableFeature(int id, boolean enable);

    /**
     * асинхронная команда на старт воспроизведения трека
     */
    void start(String trackId);

    /**
     * синхронная команда воспроизвести/поставить на паузу
     */
    void playOrPause();

    /**
     * следующий трек
     */
    void toNext();

    /**
     * предыдущий трек
     */
    void toPrevious();

    /**
     * перемотать на определённое кол-во мс
     */
    void seekTo(int positionInMS);

    /**
     * остановить воспроизведение
     */
    void stop();

    interface Listener {

        /**
         * обновление сессии воспроизведения при возникновении событий:
         * 1) начато воспроизведение (в первый раз)
         * 2) пауза/проигрывание
         * 3) ежесекундное обновление при измение текущего прогресса проигрывания
         */
        void onTrackSessionUpdate(MpTrackSession trackSession);

        /**
         * изменение настроек
         */
        void onFeatureChangeState(int id, boolean enabled);

        /**
         * возникновение ошибки
         */
        void onError(Exception e);
    }
}
