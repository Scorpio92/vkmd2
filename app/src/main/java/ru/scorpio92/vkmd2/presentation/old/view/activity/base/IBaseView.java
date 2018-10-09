package ru.scorpio92.vkmd2.presentation.old.view.activity.base;

import android.content.Context;


public interface IBaseView {

    Context getViewContext();

    void showProgress(boolean show);

    void showToast(String msg);
}
