package ru.scorpio92.vkmd2.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ViewUtils {

    /**
     * Замена фрагмента в контейнере
     *
     * @param containerId id контейнера в разметке базовой активности
     */
    public static void replaceFragment(FragmentManager fragmentManager, int containerId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Очистка стека фрагментов
     */
    public static void clearFragmentStack(FragmentManager fragmentManager) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Скрываем клавиатуру
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null)
            return;

        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            IBinder windowToken = focusedView.getWindowToken();
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    /**
     * Показываем тост
     */
    public static void showToast(Context context, String text) {
        if (context != null && text != null && !text.equals(""))
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void startActivityAndClearStack(Context context, Class activity) {
        if (context == null || activity == null)
            return;
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
