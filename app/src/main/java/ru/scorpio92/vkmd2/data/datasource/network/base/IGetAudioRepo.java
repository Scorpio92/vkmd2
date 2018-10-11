package ru.scorpio92.vkmd2.data.datasource.network.base;


import java.util.List;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.data.entity.OnlineTrack;
import ru.scorpio92.vkmd2.data.entity.Track;

public interface IGetAudioRepo {

    Observable<List<Track>> getAccountAudio(int offset);

    Observable<List<OnlineTrack>> getSearchAudio(String uid, String query);
}
