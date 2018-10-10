package ru.scorpio92.vkmd2;

import android.app.Application;

/**
 * Базовый класс который поддерживает отслеживание своего состояния с помощью
 * AppWatcher
 */
public abstract class AbstractApplication extends Application implements IAppWatcher {

    /**
     * При инициализации приложения предоставляем реализацию IAppWatcher
     * Вызываем метод onInitApp() для начала инициализации при старте приложения,
     * до старта каких-либо экранов и сервисов
     */
    @Override
    public void onCreate() {
        super.onCreate();
        onInitApp();
    }
}
