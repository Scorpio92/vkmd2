package ru.scorpio92.vkmd2.domain.usecase;

import io.reactivex.Observable;
import ru.scorpio92.vkmd2.domain.base.AbstractUseCase;
import ru.scorpio92.vkmd2.domain.datasource.IAuthInfoRepository;
import ru.scorpio92.vkmd2.domain.entity.AuthInfo;

public class GetAuthInfoUseCase extends AbstractUseCase<AuthInfo> {

    private IAuthInfoRepository authInfoRepository;

    public GetAuthInfoUseCase(IAuthInfoRepository authInfoRepository) {
        this.authInfoRepository = authInfoRepository;
    }

    @Override
    public Observable<AuthInfo> provideObservable() throws Exception {
        return authInfoRepository.getAuthInfo().toObservable();
    }
}
