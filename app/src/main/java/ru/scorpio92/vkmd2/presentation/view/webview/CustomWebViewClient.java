package ru.scorpio92.vkmd2.presentation.view.webview;

import android.graphics.Bitmap;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.Constants.AUDIO_URL;
import static ru.scorpio92.vkmd2.Constants.LOGIN_URL;

public class CustomWebViewClient extends WebViewClient {

    private WebViewClientCallback callback;

    public interface WebViewClientCallback {
        void onPageBeginLoading();

        void onAuthPageLoaded();

        void onAudioPageFinishLoad(String cookie);

        void onError();
    }


    CustomWebViewClient(WebViewClientCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        callback.onPageBeginLoading();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return super.shouldOverrideUrlLoading(webView, url);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        Logger.log("onPageFinished", url);
        if (callback != null) {
            if (url.contains(LOGIN_URL)) {
                callback.onAuthPageLoaded();
            } else if (url.equals(AUDIO_URL)) {
                final String cookies = CookieManager.getInstance().getCookie(url);
                if(cookies == null) {
                    callback.onError();
                } else {
                    callback.onAudioPageFinishLoad(cookies);
                }
                //view.loadUrl("javascript:console.log('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            } else {
                callback.onAuthPageLoaded();
            }
        }
    }
}