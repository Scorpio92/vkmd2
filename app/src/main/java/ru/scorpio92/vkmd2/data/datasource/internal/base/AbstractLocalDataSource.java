package ru.scorpio92.vkmd2.data.datasource.internal.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public abstract class AbstractLocalDataSource {

    private static WeakReference<Context> sContextWeakReference;
    private static ISecurityProvider sSecurityProvider;
    private static volatile SoftReference<AndroidPrivateStorage> sStorageSoftReference;

    public static void initialize(@NonNull Context context, @Nullable ISecurityProvider securityProvider) {
        sContextWeakReference = new WeakReference<>(context);
        sSecurityProvider = securityProvider;
    }

    public static void close() {
        sSecurityProvider = null;
        if (sContextWeakReference != null) {
            sContextWeakReference.clear();
            sContextWeakReference = null;
        }
        if (sStorageSoftReference != null) {
            sStorageSoftReference.clear();
            sStorageSoftReference = null;
        }
    }

    protected abstract String provideStoreName();

    protected abstract boolean enableEncryption();

    private AndroidPrivateStorage getStorage() throws NullPointerException {
        if (sStorageSoftReference == null || sStorageSoftReference.get() == null) {
            sStorageSoftReference = new SoftReference<>(new AndroidPrivateStorage(sContextWeakReference.get(), sSecurityProvider));
        }
        AndroidPrivateStorage androidPrivateStorage = sStorageSoftReference.get();
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
