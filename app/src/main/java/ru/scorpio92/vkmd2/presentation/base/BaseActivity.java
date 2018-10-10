package ru.scorpio92.vkmd2.presentation.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.scorpio92.vkmd2.AbstractApplication;
import ru.scorpio92.vkmd2.IAppWatcher;

/**
 * Базовый класс активности
 * Переопределяем все базовые методы IBaseView, т.к. в потомке они могут не понадобится
 */
public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {

    /**
     * Экземпляр AppWatcher
     */
    @Nullable
    private IAppWatcher mAppWatcher;

    /**
     * Презентер привязанный к данной активности
     */
    @Nullable
    private P mPresenter;

    /**
     * Попытка немного переделать стнадартный процесс инициализации вью
     * Нет необходимости думать над конкретикой в реализации onCreate
     * Достаточно переопределить bindLayout() для предоставления разметки вью
     * Далее инициализировать элементы данной разметки в методе initUI()
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //пытаемся инициализировать экземпляр AppWatcher, при условии,
        //что класс Application имплементировал интерфейс IAppWatcher
        try {
            mAppWatcher = ((AbstractApplication) getApplication());
            //если при создании активности требуется заново инициализировать глобальные объекты
            if (retryAppInitOnCreate()) {
                mAppWatcher.onInitApp();
            }
        } catch (Exception e) {
            //Тут я специально подавил исключение, т.к. использование IAppWatcher в приложении
            //это опция
            e.getMessage();
        }
        //для подлкючения разметки достаточно переопределить метод bindLayout()
        setLayout();
        //инициализируем все остальные элементы вью в методе initUI()
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //При старте активности, если презентер ещё не был привязан, привзязываем его
        if (mPresenter == null)
            mPresenter = bindPresenter();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onStart();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onPostCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //сообщаем презентеру текущий этап жизненного цикла
        if (checkPresenterState())
            mPresenter.onDestroy();
    }

    @NonNull
    @Override
    public Context getViewContext() {
        return this;
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
     * Получить экземпляр AppWatcher
     */
    @Nullable
    protected IAppWatcher getAppWatcher() {
        return mAppWatcher;
    }

    /**
     * При выходе из приложения стандартным способом (через finishApp()) глобальные объекты
     * уничтожаются. При повторном разворачивании приложения из стека, повторная инициализация
     * Application (метод onCreate) не вызовется, поэтому, в таком случае, надо инициализировать
     * глобальные объекты вызовом из начальной активности приложения
     * @return
     */
    protected boolean retryAppInitOnCreate() {
        return false;
    }

    /**
     * Вызов глобального метода finishApp для завершения работы всего приложения и освобождения
     * глобальных ресурсов, таких как БД, внутреннее хранилище и т.д.
     * Стоит использовать к примеру в корневой активности при вызове диалога выхода из приложения
     */
    protected void finishApp() {
        //освобождаем глобальные ресурсы
        if (mAppWatcher != null)
            mAppWatcher.finishApp();
        //завершаем текущую активность
        this.finish();
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
     * Привязка xml-разметки, вызывается в onCreate
     */
    private void setLayout() {
        Integer resId = bindLayout();
        if (resId != null)
            setContentView(resId);
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
    protected void initUI() {

    }
}
