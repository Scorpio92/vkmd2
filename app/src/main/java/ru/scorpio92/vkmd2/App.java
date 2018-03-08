package ru.scorpio92.vkmd2;

import android.app.Application;

import ru.scorpio92.vkmd2.data.repository.db.base.AppDatabase;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void init() {
        AppDatabase.initDB(getApplicationContext());
    }

    public static void finish() {
        AppDatabase.closeDB();
    }
}