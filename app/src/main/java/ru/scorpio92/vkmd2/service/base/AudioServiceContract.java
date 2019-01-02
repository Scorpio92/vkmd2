package ru.scorpio92.vkmd2.service.base;

public class AudioServiceContract {

    public static final String AUDIO_SERVICE_BROADCAST = "BROADCAST.AUDIO_SERVICE";

    public enum SenderEvent {
        GET_INFO,
        LOOP_FEATURE,
        RANDOM_FEATURE,
        PLAY,
        PAUSE,
        PLAY_OR_PAUSE,
        STOP,
        NEXT,
        PREV,
        SEEK_TO
    }

    public enum ReceiverEvent {
        PROVIDE_INFO,
        LOOP_ENABLED,
        LOOP_DISABLED,
        RANDOM_ENABLED,
        RANDOM_DISABLED,
        PREPARE_FOR_PLAY, //AUDIO_TRACK_NAME_PARAM, AUDIO_TRACK_ARTIST_PARAM, AUDIO_TRACK_DURATION_PARAM
        START_PLAY,
        PROGRESS_UPDATE, //AUDIO_TRACK_PROGRESS_PARAM
        PAUSE,
        STOP_SERVICE,
        ERROR
    }
}
