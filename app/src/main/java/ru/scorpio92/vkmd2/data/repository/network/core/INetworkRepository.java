package ru.scorpio92.vkmd2.data.repository.network.core;


public interface INetworkRepository {

    void execute(RequestSpecification requestSpecification);

    void cancel();
}
