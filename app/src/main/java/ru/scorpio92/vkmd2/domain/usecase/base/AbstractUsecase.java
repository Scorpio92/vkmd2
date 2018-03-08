package ru.scorpio92.vkmd2.domain.usecase.base;

import android.os.Handler;
import android.os.Looper;

import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;


public abstract class AbstractUsecase implements IAbstractUsecase {

    private IExecutor executor;
    private Handler handler;

    public AbstractUsecase() {
        this.executor = provideExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    protected abstract IExecutor provideExecutor();

    public abstract void run();

    protected abstract void onInterrupt();

    @Override
    public void execute() {
        executor.execute(this);
    }

    @Override
    public void cancel() {
        onInterrupt();
    }

    protected void runOnUI(Runnable runnable) {
        handler.post(runnable);
    }
}
