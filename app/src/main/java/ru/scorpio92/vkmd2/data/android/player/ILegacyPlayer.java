package ru.scorpio92.vkmd2.data.android.player;

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
