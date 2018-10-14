package ru.scorpio92.vkmd2.presentation.sync;

import ru.scorpio92.vkmd2.presentation.base.IBasePresenter;
import ru.scorpio92.vkmd2.presentation.base.IBaseView;

public interface IContract {

    interface View extends IBaseView {

        void updateProgressText(String text);

        void showMusicActivity();
    }

    interface Presenter extends IBasePresenter {

        void synchronize(int count);
    }
}
