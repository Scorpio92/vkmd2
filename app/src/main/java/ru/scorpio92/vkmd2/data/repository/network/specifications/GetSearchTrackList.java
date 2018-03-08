package ru.scorpio92.vkmd2.data.repository.network.specifications;

import android.util.Pair;

import java.util.Arrays;

import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;

import static ru.scorpio92.vkmd2.Constants.AUDIO_URL;


public class GetSearchTrackList extends RequestSpecification {

    public GetSearchTrackList(String cookie, String searchQuery) {
        super(AUDIO_URL + "?act=search&q=" + searchQuery);
        setHeaders(Arrays.asList(new Pair<>("cookie", cookie)));
        setConnectionTimeout(3000);
    }
}