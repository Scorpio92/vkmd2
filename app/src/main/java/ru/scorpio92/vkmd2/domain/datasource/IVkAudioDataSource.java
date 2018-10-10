package ru.scorpio92.vkmd2.domain.datasource;

import java.util.List;

import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.Track;

public interface IVkAudioDataSource {

    Single<List<Track>> getAccountAudio(int offset);

    Single<List<Track>> searchAudio(String uid, String query);
}
