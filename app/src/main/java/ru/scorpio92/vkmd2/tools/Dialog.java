package ru.scorpio92.vkmd2.tools;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import ru.scorpio92.vkmd2.R;


public class Dialog {

    public static AlertDialog.Builder getAlertDialogBuilder(String title, String msg, Activity act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act, R.style.AlertDialogStyle);
        builder.setTitle(title).setMessage(msg);
        return builder;
    }
}
