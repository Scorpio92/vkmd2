package ru.scorpio92.vkmd2.data.repository.db.base;

import android.support.annotation.Nullable;

import ru.scorpio92.vkmd2.data.entity.Track;


public interface ITrackProvider {

    @Nullable
    Track getTrackByTrackId(String trackId);

    @Nullable
    Track getPreviousTrack(Track currentTrack);

    @Nullable
    Track getNextTrack(Track currentTrack);

    void setRandomEnabled(boolean randomEnabled);
}
