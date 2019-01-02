package ru.scorpio92.vkmd2.data.repository;

import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.datasource.IAuthInfoRepository;
import ru.scorpio92.vkmd2.domain.datasource.ICookieDataSource;
import ru.scorpio92.vkmd2.domain.datasource.IPermissionsDataSource;
import ru.scorpio92.vkmd2.domain.entity.AuthInfo;

public class AuthInfoRepository implements IAuthInfoRepository {

    private IPermissionsDataSource permissionsDataSource;
    private ICookieDataSource cookieDataSource;

    public AuthInfoRepository(IPermissionsDataSource permissionsDataSource, ICookieDataSource cookieDataSource) {
        this.permissionsDataSource = permissionsDataSource;
        this.cookieDataSource = cookieDataSource;
    }

    @Override
    public Single<AuthInfo> getAuthInfo() {
        return permissionsDataSource.checkPermissions()
                .flatMap(permissionsEnabled ->
                        cookieDataSource.checkCookieExists()
                                .map(cookieExists ->
                                        new AuthInfo(permissionsEnabled, cookieExists)));
    }
}
