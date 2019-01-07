package ru.scorpio92.vkmd2.data.android.player;

/**
 * Провайдер треков
 */
public interface IMpTrackProvider {

    MpTrack getTrackById(String trackId) throws Exception;

    MpTrack getNextTrack(String currentTrackId) throws Exception;

    MpTrack getPreviousTrack(String currentTrackId) throws Exception;

    String getRandomTrackId() throws Exception;
}
