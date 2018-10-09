package ru.scorpio92.vkmd2.presentation.old.presenter.base;


public interface ISyncPresenter extends IBasePresenter {
    void parsePageSourceCode(String cookie, int count);
}
