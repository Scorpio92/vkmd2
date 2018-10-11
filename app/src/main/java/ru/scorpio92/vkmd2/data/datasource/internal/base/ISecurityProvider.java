package ru.scorpio92.vkmd2.data.datasource.internal.base;

public interface ISecurityProvider {

    String encrypt(String data) throws Exception;

    String decrypt(String encrypted) throws Exception;
}
