package ru.scorpio92.vkmd2.presentation.auth.webview;

import android.graphics.Bitmap;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static ru.scorpio92.vkmd2.BuildConfig.AUDIO_URL;
import static ru.scorpio92.vkmd2.BuildConfig.BASE_URL;
import static ru.scorpio92.vkmd2.BuildConfig.FEED_URL;
import static ru.scorpio92.vkmd2.BuildConfig.LOGIN_URL;


public class CustomWebViewClient extends WebViewClient {

    private WebViewClientCallback callback;
    private boolean hasError;

    public interface WebViewClientCallback {
        void onPageBeginLoading();

        void onAuthPageLoaded();

        void onCookieReady(String cookie);

        void onBadConnection();

        void onNotAuthPageLoaded();
    }

    CustomWebViewClient(WebViewClientCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        hasError = false;
        callback.onPageBeginLoading();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return super.shouldOverrideUrlLoading(webView, url);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        if (callback != null) {
            if (!hasError) {
                if (url.contains(LOGIN_URL) || url.equals(BASE_URL) || url.equals(BASE_URL.concat("/"))) {
                    callback.onAuthPageLoaded();
                } else if (url.equals(AUDIO_URL) || url.equals(FEED_URL)) {
                    final String cookies = CookieManager.getInstance().getCookie(url);
                    if (cookies == null) {
                        callback.onBadConnection();
                    } else {
                        callback.onCookieReady(cookies);
                    }
                } else {
                    callback.onNotAuthPageLoaded();
                }
            } else {
                callback.onBadConnection();
            }
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        hasError = true;
    }
}