package ru.scorpio92.vkmd2.tools;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class LocalStorage {

    public final static String COOKIE_STORAGE = ".cookie";
    public final static String SYNC_TRACKS_COUNT_STORAGE = ".sync_tracks_count";
    public final static String SYNC_LAST_TIME_STORAGE = ".sync_last_time";
    public final static String IS_NOT_FIRST_RUN = ".first_run";
    public final static String USER_ID_STORAGE = ".uid";
    public final static String LOGIN_DIALOG_FLAG = ".ldf";

    public static void setDataInFile(Context context, String fileName, String data) throws Exception {
        FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        outputStream.write(data.getBytes());
        outputStream.close();
    }

    public static String getDataFromFile(Context context, String fileName) throws Exception {
        FileInputStream fileInputStream = context.openFileInput(fileName);
        int ch;
        StringBuilder temp = new StringBuilder();
        while ((ch = fileInputStream.read()) != -1) {
            temp.append(Character.toString((char) ch));
        }
        fileInputStream.close();
        return temp.toString();
    }

    public static boolean fileExist(Context context, String fileName) {
        String[] arrFileName = context.fileList();
        for (String item : arrFileName) {
            if (item.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean deleteFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }
}
