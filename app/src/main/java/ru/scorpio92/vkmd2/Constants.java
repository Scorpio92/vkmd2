package ru.scorpio92.vkmd2;


import android.os.Environment;

public class Constants {

    private final static String BASE_URL = "https://m.vk.com";
    public final static String LOGIN_URL = BASE_URL + "/login";
    public final static String AUDIO_URL = BASE_URL + "/audio";

    public static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "vkmd" + "/" + "db";
    public static final String DB_NAME = "vkmd.db";

    public final static int GET_AUDIO_OFFSET = 200;

    public final static String AUTHOR_URL = "http://scorpio92.ru/about";
}
