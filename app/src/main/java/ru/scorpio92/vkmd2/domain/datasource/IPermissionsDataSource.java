package ru.scorpio92.vkmd2.domain.datasource;

import io.reactivex.Single;

public interface IPermissionsDataSource {

    Single<Boolean> checkPermissions();

    void dispose();
}
