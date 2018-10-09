package ru.scorpio92.vkmd2.presentation.auth;

import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;
import ru.scorpio92.vkmd2.presentation.old.view.webview.CustomWebViewClient;

public interface IContract {

    interface View extends IBaseView {

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
         * Права предоставлены
         */
        void onPermissionGranted();

        /**
         * Пользователь прочитал диалог с предупреждением
         */
        void onUserReadAttention();
    }
}
