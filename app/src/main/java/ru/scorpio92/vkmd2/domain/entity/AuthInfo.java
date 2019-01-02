package ru.scorpio92.vkmd2.domain.entity;

/**
 * Сущность описывающая состояние авторизации в ВК (наличие куки), а также
 * факт предоставления необходимых прав приложению
 */
public class AuthInfo {

    private boolean permissionsGranted;
    private boolean userIsAuthorized;

    public AuthInfo(boolean permissionsGranted, boolean userIsAuthorized) {
        this.permissionsGranted = permissionsGranted;
        this.userIsAuthorized = userIsAuthorized;
    }

    public boolean isPermissionsGranted() {
        return permissionsGranted;
    }

    public boolean isUserIsAuthorized() {
        return userIsAuthorized;
    }
}
