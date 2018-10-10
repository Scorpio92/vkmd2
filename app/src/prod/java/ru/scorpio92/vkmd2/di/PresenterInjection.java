package ru.scorpio92.vkmd2.di;

import android.Manifest;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;

import ru.scorpio92.vkmd2.data.repository.internal.CookieDataSource;
import ru.scorpio92.vkmd2.presentation.auth.AuthPresenter;
import ru.scorpio92.vkmd2.presentation.auth.IContract;

public class PresenterInjection {

    public static AuthPresenter provideAuthPresenter(@NonNull IContract.View view) {
        return new AuthPresenter(
                view,
                new RxPermissions(view.getActivity()).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS),
                new CookieDataSource()
        );
    }
}
