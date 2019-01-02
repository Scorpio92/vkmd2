package ru.scorpio92.vkmd2.di;

import android.app.Activity;

import ru.scorpio92.vkmd2.data.datasource.internal.SyncDataSource;
import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUseCase;
import ru.scorpio92.vkmd2.domain.usecase.GetAuthInfoUseCase;
import ru.scorpio92.vkmd2.domain.usecase.SaveCookieUseCase;
import ru.scorpio92.vkmd2.domain.usecase.SyncTracksUseCase;

public class UseCaseInjection {

    public static GetAuthInfoUseCase provideGetAuthInfoUseCase(Activity activity) {
        return new GetAuthInfoUseCase(DataSourceInjection.provideAuthInfoRepository(activity));
    }

    public static SaveCookieUseCase provideSaveCookieUseCase() {
        return new SaveCookieUseCase(DataSourceInjection.provideCookieDataSource());
    }

    public static SyncTracksUseCase provideSyncTracksUseCase() {
        return new SyncTracksUseCase(
                new SyncDataSource(),
                DataSourceInjection.provideTrackRepository()
        );
    }

    public static GetAccountTracksUseCase provideGetAccountTracksUseCase() {
        return new GetAccountTracksUseCase(
                new SyncDataSource(),
                DataSourceInjection.provideTrackRepository()
        );
    }
}
