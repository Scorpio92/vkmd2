package ru.scorpio92.vkmd2.domain.datasource;

import io.reactivex.Single;
import ru.scorpio92.vkmd2.domain.entity.AuthInfo;

public interface IAuthInfoRepository {

    Single<AuthInfo> getAuthInfo();
}
