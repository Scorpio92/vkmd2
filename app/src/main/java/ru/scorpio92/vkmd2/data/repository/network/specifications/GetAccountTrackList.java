package ru.scorpio92.vkmd2.data.repository.network.specifications;

import android.util.Pair;

import java.util.Arrays;

import ru.scorpio92.vkmd2.data.repository.network.core.RequestSpecification;

import static ru.scorpio92.vkmd2.Constants.AUDIO_URL;


public class GetAccountTrackList extends RequestSpecification {

    public GetAccountTrackList(String cookie, int offset) {
        super(AUDIO_URL + "?offset=" + offset);
        setHeaders(Arrays.asList(new Pair<>("cookie", cookie)));
        setConnectionTimeout(3000);
    }
}
