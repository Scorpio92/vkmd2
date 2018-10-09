package ru.scorpio92.vkmd2;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ru.scorpio92.vkmd2.data.repository.db.base.old.AppDatabase;


public class App extends AbstractApplication {

    public static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APP_FOLDER;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MultiDex.install(getApplicationContext());
    }

    public static void finish() {
        AppDatabase.closeDB();
    }

    @Override
    public void onInitApp(Context context) {
        AppDatabase.initDB(context.getApplicationContext());
    }

    @Override
    public void finishApp() {
        AppDatabase.closeDB();
    }
}