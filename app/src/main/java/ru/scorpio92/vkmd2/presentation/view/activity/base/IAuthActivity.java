package ru.scorpio92.vkmd2.presentation.view.activity.base;


public interface IAuthActivity extends IBaseView {
    void showWebView();

    void showSyncActivity(String cookie);

    void showMusicActivity();
}
