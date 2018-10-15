package ru.scorpio92.vkmd2.presentation.entity.converter;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.entity.UiTrack;

public class TrackConverter {

    public static UiTrack convertTrackToUiTrack(Track track) {
        return (UiTrack) track;
    }

    public static List<UiTrack> convertTrackListToUiTrackList(List<Track> trackList) {
        List<UiTrack> uiTrackList = new ArrayList<>();
        for (Track track : trackList) {
            uiTrackList.add(convertTrackToUiTrack(track));
        }
        return uiTrackList;
    }
}
