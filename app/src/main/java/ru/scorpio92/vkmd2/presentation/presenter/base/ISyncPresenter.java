package ru.scorpio92.vkmd2.presentation.presenter.base;


public interface ISyncPresenter extends IBasePresenter {
    void parsePageSourceCode(String cookie, int count);
}
