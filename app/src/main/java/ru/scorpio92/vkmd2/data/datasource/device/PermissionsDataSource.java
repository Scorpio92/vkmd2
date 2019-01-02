package ru.scorpio92.vkmd2.data.datasource.device;

import android.Manifest;
import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import ru.scorpio92.vkmd2.domain.datasource.IPermissionsDataSource;

public class PermissionsDataSource implements IPermissionsDataSource {

    private Activity activity;
    private Disposable permissionsDisposable;


    public PermissionsDataSource(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Single<Boolean> checkPermissions() {
        return new RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NETWORK_STATE)
                .firstOrError().doOnSubscribe(disposable -> permissionsDisposable = disposable);
    }

    @Override
    public void dispose() {
        if(permissionsDisposable != null && !permissionsDisposable.isDisposed()) {
            permissionsDisposable.dispose();
        }
    }
}
