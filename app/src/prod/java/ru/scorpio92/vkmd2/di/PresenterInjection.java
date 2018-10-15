package ru.scorpio92.vkmd2.di;

import android.Manifest;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;

import ru.scorpio92.vkmd2.data.datasource.internal.CookieDataSource;
import ru.scorpio92.vkmd2.presentation.auth.AuthPresenter;
import ru.scorpio92.vkmd2.presentation.auth.IContract;
import ru.scorpio92.vkmd2.presentation.main.tracklist.TrackListPresenter;
import ru.scorpio92.vkmd2.presentation.sync.SyncPresenter;

public class PresenterInjection {

    public static AuthPresenter provideAuthPresenter(@NonNull IContract.View view) {
        return new AuthPresenter(
                view,
                new RxPermissions(view.getActivity()).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.ACCESS_NETWORK_STATE),
                new CookieDataSource()
        );
    }

    public static SyncPresenter provideSyncPresenter(@NonNull ru.scorpio92.vkmd2.presentation.sync.IContract.View view) {
        return new SyncPresenter(view, UseCaseInjection.provideSyncTracksUseCase());
    }

    public static TrackListPresenter provideTrackListPresenter(@NonNull ru.scorpio92.vkmd2.presentation.main.tracklist.IContract.View view) {
        return new TrackListPresenter(view, UseCaseInjection.provideGetAccountTracksUseCase());
    }
}
