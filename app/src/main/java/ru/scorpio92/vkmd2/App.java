package ru.scorpio92.vkmd2;

import android.os.Environment;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import ru.scorpio92.vkmd2.data.repository.db.base.old.AppDatabase;
import ru.scorpio92.vkmd2.data.repository.internal.base.AbstractLocalDataSource;


public class App extends AbstractApplication {

    public static final String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APP_FOLDER;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        MultiDex.install(getApplicationContext());
    }

    public static void finish() {
        AppDatabase.closeDB();
    }

    @Override
    public void onInitApp() {
        AbstractLocalDataSource.initialize(getApplicationContext(), null);
        AppDatabase.initDB(getApplicationContext());
    }

    @Override
    public void finishApp() {
        AbstractLocalDataSource.close();
        AppDatabase.closeDB();
    }
}