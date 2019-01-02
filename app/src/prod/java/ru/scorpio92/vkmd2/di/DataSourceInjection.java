package ru.scorpio92.vkmd2.di;

import android.app.Activity;

import ru.scorpio92.vkmd2.data.datasource.db.TrackDbDataSource;
import ru.scorpio92.vkmd2.data.datasource.device.PermissionsDataSource;
import ru.scorpio92.vkmd2.data.datasource.internal.CookieDataSource;
import ru.scorpio92.vkmd2.data.datasource.internal.TracksMemoryCacheDataSource;
import ru.scorpio92.vkmd2.data.datasource.network.VkAudioDataSource;
import ru.scorpio92.vkmd2.data.repository.AuthInfoRepository;
import ru.scorpio92.vkmd2.data.repository.TrackRepository;
import ru.scorpio92.vkmd2.domain.datasource.IAuthInfoRepository;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;
import ru.scorpio92.vkmd2.domain.datasource.IPermissionsDataSource;

public class DataSourceInjection {

    //////////////////////////////DataSources///////////////////////////////////////////////////////

    public static ICookieDataSource provideCookieDataSource() {
        return new CookieDataSource();
    }

    public static IPermissionsDataSource providePermissionsDataSource(Activity activity) {
        return new PermissionsDataSource(activity);
    }

    //////////////////////////////Repositories//////////////////////////////////////////////////////

    public static IAuthInfoRepository provideAuthInfoRepository(Activity activity) {
        return new AuthInfoRepository(
                providePermissionsDataSource(activity),
                provideCookieDataSource()
        );
    }

    public static TrackRepository provideTrackRepository() {
        return new TrackRepository(
                provideCookieDataSource(),
                new VkAudioDataSource(),
                new TrackDbDataSource(),
                new TracksMemoryCacheDataSource()
        );
    }
}
