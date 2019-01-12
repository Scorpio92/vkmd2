package ru.scorpio92.vkmd2.data.android.player.base;

/**
 * Интерфейс для реализации аудиоплеера
 * На данный момент бует скрывать реализацию android.media.MediaPlayer
 */
public interface ILegacyPlayer {

    /**
     * проверка иёт ли в данный момент воспроизведение
     */
    boolean isPlaying();

    /**
     * асинхронная команда на старт воспроизведения трека
     */
    void start(String trackURI);

    /**
     * приостановить воспроизведение
     */
    void pause();

    /**
     * продолжить воспроизведение
     */
    void resume();

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
         * воспроизведение запущено
         */
        void onPlayStarted();

        /**
         * воспроизведение завершено
         */
        void onPlayCompleted();

        /**
         * возникла ошибка
         */
        void onError(Exception e);
    }
}
