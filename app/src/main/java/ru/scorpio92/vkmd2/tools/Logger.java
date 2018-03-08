package ru.scorpio92.vkmd2.tools;

import android.util.Log;

import ru.scorpio92.vkmd2.BuildConfig;

public class Logger {

    public static void log(String msg) {
        if (BuildConfig.DEBUG)
            Log.i("LOGGER", msg);
    }

    public static void log(String tag, String msg) {
        if (BuildConfig.DEBUG)
            Log.i("LOGGER", tag + ":\n" + msg);
    }

    public static void error(String error) {
        if(BuildConfig.DEBUG)
            Log.e("LOGGER", "OOPS...EXCEPTION:\n" + error);
    }

    public static void error(String tag, String error) {
        if(BuildConfig.DEBUG)
            Log.e("LOGGER", tag + ":\n" + error);
    }

    public static void error(Exception e) {
        if(BuildConfig.DEBUG)
            Log.e("LOGGER", "OOPS...EXCEPTION!", e);
    }
}
