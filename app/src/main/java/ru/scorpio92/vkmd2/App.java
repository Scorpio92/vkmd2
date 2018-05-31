package ru.scorpio92.vkmd2;

import android.app.Application;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;


public class App extends MultiDexApplication {

    public static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APP_FOLDER;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    public void init() {
        AppDatabase.initDB(getApplicationContext());
    }

    public static void finish() {
        AppDatabase.closeDB();
    }
}