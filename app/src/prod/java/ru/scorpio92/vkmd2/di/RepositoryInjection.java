package ru.scorpio92.vkmd2.di;

import ru.scorpio92.vkmd2.data.datasource.db.TrackDbDataSource;
import ru.scorpio92.vkmd2.data.datasource.internal.CookieDataSource;
import ru.scorpio92.vkmd2.data.datasource.internal.TracksMemoryCacheDataSource;
import ru.scorpio92.vkmd2.data.datasource.network.VkAudioDataSource;
import ru.scorpio92.vkmd2.data.repository.TrackRepository;

public class RepositoryInjection {

    public static TrackRepository provideTrackRepository() {
        return new TrackRepository(
                new CookieDataSource(),
                new VkAudioDataSource(),
                new TrackDbDataSource(),
                new TracksMemoryCacheDataSource()
        );
    }
}
