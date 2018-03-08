package ru.scorpio92.vkmd2.data.repository.db.base;

import android.support.annotation.Nullable;

import ru.scorpio92.vkmd2.data.entity.Track;


public interface ITrackProvider {

    int getTracksCount();

    @Nullable
    Track getTrackByTrackId(String trackId);

    @Nullable
    Track getTrackByPosition(int position);
}
