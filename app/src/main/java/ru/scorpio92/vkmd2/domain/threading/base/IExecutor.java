package ru.scorpio92.vkmd2.domain.threading.base;


import ru.scorpio92.vkmd2.domain.usecase.base.AbstractUsecase;

public interface IExecutor {

    void execute(final AbstractUsecase usecase);

    void shutdownExecutor();
}
