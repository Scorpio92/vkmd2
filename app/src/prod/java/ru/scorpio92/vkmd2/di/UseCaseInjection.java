package ru.scorpio92.vkmd2.di;

import ru.scorpio92.vkmd2.data.datasource.internal.SyncDataSource;
import ru.scorpio92.vkmd2.domain.usecase.GetAccountTracksUseCase;
import ru.scorpio92.vkmd2.domain.usecase.SyncTracksUseCase;

public class UseCaseInjection {

    public static SyncTracksUseCase provideSyncTracksUseCase() {
        return new SyncTracksUseCase(
                new SyncDataSource(),
                RepositoryInjection.provideTrackRepository()
        );
    }

    public static GetAccountTracksUseCase provideGetAccountTracksUseCase() {
        return new GetAccountTracksUseCase(
                new SyncDataSource(),
                RepositoryInjection.provideTrackRepository()
        );
    }
}
