package ru.scorpio92.vkmd2.presentation.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Базовый класс фрагмента
 * Переопределяем все базовые методы IBaseView, т.к. в потомке они могут не понадобится
 */
public abstract class BaseFragment<P extends IBasePresenter> extends Fragment implements IBaseView {

    /**
     * Колбэк активности для отслеживания событий во фрагменте
     */
    @Nullable
    private IFragmentListener mFragmentListener;

    /**
     * Презентер привязанный к данному фрагменту
     */
    @Nullable
    private P mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFragmentListener = (IFragmentListener) context;
        } catch (ClassCastException e) {
            //Тут я специально подавил исключение, т.к. использование IFragmentListener в активности
            //это опция. Не каждая активность содержит в себе фрагменты
            e.getMessage();
        }
    }

    /**
     * Попытка немного переделать стнадартный процесс инициализации вью
     * Нет необходимости думать над конкретикой в реализации onCreateView
     * Достаточно переопределить bindLayout() для предоставления разметки вью
     * Далее инициализировать элементы данной разметки в методе initUI()
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        Integer resId = bindLayout();
        if (resId == null) {
            view = super.onCreateView(inflater, container, savedInstanceState);
        } else {
            view = inflater.inflate(resId, container, false);
            initUI(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //При старте фрагмента, если презентер ещё не был привязан, привзязываем его
        //в отличае от активности, при инициализации фрагмента onStart вызывается после onViewCreated
        if (mPresenter == null)
            mPresenter = bindPresenter();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onPostCreate();
    }

    @Override
    public void onStart() {
        super.onStart();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mFragmentListener = null;

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onDestroy();
    }

    @Nullable
    @Override
    public Context getViewContext() {
        return getContext();
    }

    @Override
    public void showProgress() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void hideProgress() {
        //переопределить и написать свою реализацию (при необходимости)
    }

    @Override
    public void onError(@NonNull String error) {
        //переопределить и написать свою реализацию (при необходимости)
    }

    /**
     * Привязка презентера к вью
     * Вью может инциализировать в данном методе экземпляр того презентера,
     * который необходимо привязать к данной вью
     */
    @Nullable
    protected P bindPresenter() {
        return null;
    }

    /**
     * Получаем экземпляр текущего привязанного к вью презентера
     */
    @Nullable
    protected P getPresenter() {
        return mPresenter;
    }

    /**
     * Проверка жив ли презентер
     *
     * @return true, если жив
     */
    protected boolean checkPresenterState() {
        return mPresenter != null;
    }

    /**
     * Получить колбэк активности для оповещения её о выполняемых действиях во фрагментах
     */
    @Nullable
    protected IFragmentListener getFragmentListener() {
        return mFragmentListener;
    }

    /**
     * Проверка наличия колбэка активности
     */
    protected boolean checkFragmentListener() {
        return mFragmentListener != null;
    }

    /**
     * id разметки
     *
     * @return R.layout.<xml_file>
     */
    @Nullable
    protected Integer bindLayout() {
        return null;
    }

    /**
     * Метод инициализации UI
     * Вызывается непосредственно после подключения разметки
     */
    protected void initUI(@NonNull View view) {

    }
}
