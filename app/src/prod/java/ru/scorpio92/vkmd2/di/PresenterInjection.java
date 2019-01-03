package ru.scorpio92.vkmd2.di;

import android.support.annotation.NonNull;

import ru.scorpio92.vkmd2.presentation.auth.AuthPresenter;
import ru.scorpio92.vkmd2.presentation.auth.IContract;
import ru.scorpio92.vkmd2.presentation.main.fragment.player.PlayerPresenter;
import ru.scorpio92.vkmd2.presentation.main.fragment.search.SearchPresenter;
import ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.TrackListPresenter;
import ru.scorpio92.vkmd2.presentation.sync.SyncPresenter;

public class PresenterInjection {

    public static AuthPresenter provideAuthPresenter(@NonNull IContract.View view) {
        return new AuthPresenter(
                view,
                UseCaseInjection.provideGetAuthInfoUseCase(view.getActivity()),
                UseCaseInjection.provideSaveCookieUseCase()
        );
    }

    public static SyncPresenter provideSyncPresenter(@NonNull ru.scorpio92.vkmd2.presentation.sync.IContract.View view) {
        return new SyncPresenter(view, UseCaseInjection.provideSyncTracksUseCase());
    }

    public static PlayerPresenter providePlayerPresenter(@NonNull ru.scorpio92.vkmd2.presentation.main.fragment.player.IContract.View view) {
        return new PlayerPresenter(view);
    }

    public static TrackListPresenter provideTrackListPresenter(@NonNull ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.IContract.View view) {
        return new TrackListPresenter(view, UseCaseInjection.provideGetAccountTracksUseCase());
    }

    public static SearchPresenter provideSearchPresenter(@NonNull ru.scorpio92.vkmd2.presentation.main.fragment.search.IContract.View view) {
        return new SearchPresenter(view);
    }
}
