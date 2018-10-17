package ru.scorpio92.vkmd2.presentation.entity.converter;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.entity.UiTrack;

public class TrackConverter {

    public static UiTrack convertTrackToUiTrack(Track track) {
        UiTrack uiTrack = new UiTrack();
        uiTrack.setTrackId(track.getTrackId());
        uiTrack.setUserId(track.getUserId());
        uiTrack.setArtist(track.getArtist());
        uiTrack.setName(track.getName());
        uiTrack.setDuration(track.getDuration());
        uiTrack.setUrlAudio(track.getUrlAudio());
        uiTrack.setUrlImage(track.getUrlImage());
        uiTrack.setStatus(track.getStatus());
        uiTrack.setSavedPath(track.getSavedPath());
        return uiTrack;
    }

    public static List<UiTrack> convertTrackListToUiTrackList(List<Track> trackList) {
        List<UiTrack> uiTrackList = new ArrayList<>();
        for (Track track : trackList) {
            uiTrackList.add(convertTrackToUiTrack(track));
        }
        return uiTrackList;
    }
}
