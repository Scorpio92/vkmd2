package ru.scorpio92.vkmd2.service.base;

public abstract class AudioServiceContract {

    public static final String AUDIO_SERVICE_BROADCAST = "BROADCAST.AUDIO_SERVICE";

    public enum SenderEvent {
        GET_CURRENT_TRACK, //получить информацию по текущему треку
        LOOP_FEATURE_CHECKED, //нажата кнопка Loop
        RANDOM_FEATURE_CHECKED, //нажата кнопка Random
        PLAY_OR_PAUSE, //нажата кнопка Play или Pause
        NEXT, //нажата кнопка Next
        PREV, //нажата кнопка Previous
        SEEK_TO //пользователь нажал на перемотку
    }

    public enum ReceiverEvent {
        TRACK_UPDATE, //информация по треку при его смене/проигрывании, либо запросе на инициализации
        LOOP_ENABLED,
        LOOP_DISABLED,
        RANDOM_ENABLED,
        RANDOM_DISABLED,
        PLAY,
        PAUSE,
        PLAYER_STOPPED, //плеер остановлен (например, из уведомления)
        ERROR //возникла ошибка при проигрывании
    }
}
