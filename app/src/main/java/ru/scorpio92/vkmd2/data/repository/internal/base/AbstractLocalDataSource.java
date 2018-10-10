package ru.scorpio92.vkmd2.data.repository.internal.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

public abstract class AbstractLocalDataSource {

    private static volatile WeakReference<AndroidPrivateStorage> storageWeakReference;

    public static void initialize(@NonNull Context context, @Nullable ISecurityProvider securityProvider) {
        storageWeakReference = new WeakReference<>(new AndroidPrivateStorage(context, securityProvider));
    }

    public static void close() {
        if(storageWeakReference != null) {
            storageWeakReference.clear();
            storageWeakReference = null;
        }
    }

    protected abstract String provideStoreName();

    protected abstract boolean enableEncryption();

    private AndroidPrivateStorage getStorage() throws NullPointerException {
        AndroidPrivateStorage androidPrivateStorage = storageWeakReference.get();
        androidPrivateStorage.setEncryptionEnabled(enableEncryption());
        return androidPrivateStorage;
    }

    protected void saveData(String data) throws Exception {
        getStorage().setDataInFile(provideStoreName(), data);
    }

    protected String getData() throws Exception {
        return getStorage().getDataFromFile(provideStoreName());
    }

    protected boolean checkStoreExists() throws Exception {
        return getStorage().fileExist(provideStoreName());
    }

    protected void deleteStore() throws Exception {
        getStorage().deleteFile(provideStoreName());
    }
}
