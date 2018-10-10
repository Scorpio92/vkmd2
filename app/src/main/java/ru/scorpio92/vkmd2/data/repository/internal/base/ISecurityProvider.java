package ru.scorpio92.vkmd2.data.repository.internal.base;

public interface ISecurityProvider {

    String encrypt(String data) throws Exception;

    String decrypt(String encrypted) throws Exception;
}
