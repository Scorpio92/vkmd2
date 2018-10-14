package ru.scorpio92.vkmd2.presentation.auth;

import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;
import ru.scorpio92.vkmd2.presentation.auth.webview.CustomWebViewClient;

public interface IContract {

    interface View extends IBaseView {

        /**
         * Права предоставлены
         */
        void onPermissionNotGranted();

        /**
         * начать загрузку страницы VK во WebView
         */
        void loadVkPage();

        /**
         * показать диалог предупреждения
         */
        void showAttentionDialog();

        /**
         * показать страничку VK для авторизации
         */
        void showVkPage();

        /**
         * показать экран синхронизации
         */
        void showSyncActivity();

        /**
         * Показать главную активность
         */
        void showMusicActivity();
    }

    interface Presenter extends IBasePresenter, CustomWebViewClient.WebViewClientCallback {

        /**
         * Пользователь прочитал диалог с предупреждением
         */
        void onUserReadAttention();
    }
}
