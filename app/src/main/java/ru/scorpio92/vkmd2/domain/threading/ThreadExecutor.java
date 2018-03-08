package ru.scorpio92.vkmd2.domain.threading;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.scorpio92.vkmd2.domain.threading.base.IExecutor;
import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;

public class ThreadExecutor implements IExecutor {

    /**
     * Экземпляр легковесного менеджера потоков, с рамзером пула == 1
     */
    private static volatile ThreadExecutor lightThreadExecutor;
    /**
     * Экземпляр тяжеловестного менеджера потоков, с размером пула зависящим от кол-ва ядер процессора
     */
    private static volatile ThreadExecutor heavyThreadExecutor;

    private static final int KEEP_ALIVE_TIME = 30;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>(128);

    /**
     * Флаг показывающий что будет создан легковестный Executor
     */
    private Boolean singleTaskExecutor;

    private ThreadPoolExecutor mThreadPoolExecutor;


    private ThreadExecutor(boolean singleTaskExecutor) {
        this.singleTaskExecutor = singleTaskExecutor;
        initThreadPoolExecutor();
    }

    private void initThreadPoolExecutor() {
        if (singleTaskExecutor) {
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    1,
                    1,
                    KEEP_ALIVE_TIME,
                    TIME_UNIT,
                    WORK_QUEUE);
        } else {
            mThreadPoolExecutor = new ThreadPoolExecutor(
                    getCorePoolSize(),
                    getCorePoolSize(),
                    KEEP_ALIVE_TIME,
                    TIME_UNIT,
                    WORK_QUEUE);
        }
    }

    @Override
    public void execute(final AbstractUsecase usecase) {
        if (mThreadPoolExecutor.isShutdown() || mThreadPoolExecutor.isTerminated()) {
            initThreadPoolExecutor();
        }

        mThreadPoolExecutor.submit(usecase::run);
    }


    @Override
    public void shutdownExecutor() {
        if (mThreadPoolExecutor != null && !mThreadPoolExecutor.isShutdown()) {
            mThreadPoolExecutor.shutdownNow();
        }
    }

    /**
     * Определяем размер пула потоков, исходя из кол-ва ядер процессора
     *
     * @return
     */
    private int getCorePoolSize() {
        int realCores = Runtime.getRuntime().availableProcessors();
        return realCores + 1;
    }


    public static IExecutor getInstance(boolean singleTaskExecutor) {
        if (singleTaskExecutor) {
            if (lightThreadExecutor == null) {
                synchronized (ThreadExecutor.class) {
                    if (lightThreadExecutor == null) {
                        lightThreadExecutor = new ThreadExecutor(true);
                    }
                }
            }
            return lightThreadExecutor;
        } else {
            if (heavyThreadExecutor == null) {
                synchronized (ThreadExecutor.class) {
                    if (heavyThreadExecutor == null) {
                        heavyThreadExecutor = new ThreadExecutor(false);
                    }
                }
            }
            return heavyThreadExecutor;
        }
    }
}
