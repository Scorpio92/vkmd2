package ru.scorpio92.vkmd2.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public class NetworkUtils {

    public static void clearWebViewCache(WebView webView) {
        if (webView != null) {
            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
        }
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
                cookieSyncMngr.startSync();
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
                cookieSyncMngr.stopSync();
                cookieSyncMngr.sync();
            }
        }
    }

    public static boolean checkConnection(Context context) {
        boolean result = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            result = netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return result;
    }
}
