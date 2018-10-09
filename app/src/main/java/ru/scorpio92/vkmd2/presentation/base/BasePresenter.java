package ru.scorpio92.vkmd2.presentation.base;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Базовый класс презентера
 * Переопределяем все базовые методы IBasePresenter, т.к. в потомке они могут не понадобится
 */
public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter {

    /**
     * Сообщение об ошибке по-умолчанию
     */
    private static final String DEFAULT_UNKNOWN_ERROR = "Unknown error";

    /**
     * Вью которую обслуживает презентер
     */
    private V mView;

    public BasePresenter(@NonNull V mView) {
        this.mView = mView;
    }


    @Override
    public void onStart() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onPostCreate() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onResume() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onPause() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onStop() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onDestroy() {
        mView = null;
        //переопределить и написать свою реализацию (при необходимости)
        //ВАЖНО! в потомке не убирать вызов super() !!!
    }

    /**
     * Получить экземпляр вью
     */
    @Nullable
    protected V getView() {
        return mView;
    }

    /**
     * Проверка жива ли вью
     * @return true, если жива
     */
    protected boolean checkViewState() {
        return mView != null;
    }

    /**
     * Безопасный вызов методов вью без опаски, что вью уже померло
     * @param runnable действие которое необходимо выполнить
     */
    protected void safeViewCall(Runnable runnable) {
        if (checkViewState())
            runnable.run();
    }

    /**
     * Обработка ошибок по умолчанию
     */
    protected void handleErrors(Throwable t) {
        if (checkViewState()) {
            if (t != null) {
                if (provideAutoLogStrategy()) writeExceptionInLog(t);
                if (checkViewState()) onCustomErrorsHandle(mView, (Exception) t);
            } else {
                if (provideAutoLogStrategy()) {
                    writeExceptionInLog(new Exception("WTF. Exception is null !!!"));
                }
                String error = provideDefaultErrorMsg();
                if (error != null && !error.isEmpty())
                    mView.onError(error);
            }
        }
    }

    /**
     * Настройка логирования по-умолчанию
     * При поступлении ошибки в handleErrors(), ошибки логируются если provideAutoLogStrategy() == true
     */
    protected boolean provideAutoLogStrategy() {
        //поведение по-умолчанию - автоматическое логирование
        return true;
    }

    /**
     * Запись ошибки в лог
     * При необходимости в классе-потомке необходимо переопределить для использования своего логгера
     */
    protected void writeExceptionInLog(Throwable t) {
        //по-умолчанию показываем стэк-трейс ошибки
        t.printStackTrace();
    }

    /**
     * Показываем ошибку по-умолчанию если exception is null
     */
    protected String provideDefaultErrorMsg() {
        return DEFAULT_UNKNOWN_ERROR;
    }

    /**
     * Метод содержащий логику по кастомной обработке ошибок
     * Вызывается с гарантией mView != null && e != null
     */
    protected void onCustomErrorsHandle(@NonNull V v, @NonNull Exception e) {

    }

    /**
     * Вспомогательный метод для получения строки (например, с целью показа ошибок во вью)
     * @param resId id строкового ресурса
     * если по каким-то причинам mView == null, отдаём строку по-умолчанию
     */
    protected String getString(int resId) {
        return (checkViewState()) ? getView().getViewContext().getString(resId) : DEFAULT_UNKNOWN_ERROR;
    }
}
