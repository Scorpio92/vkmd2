package ru.scorpio92.vkmd2.presentation.old.view.webview;


import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

public class CustomWebChromeClient extends WebChromeClient {

    private MyWebChromeClientCallback callback;

    CustomWebChromeClient(MyWebChromeClientCallback callback) {
        this.callback = callback;
    }

    public interface MyWebChromeClientCallback {
        void onAudioPageSource(String code);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        String source = cm.message();
        if (callback != null)
            callback.onAudioPageSource(source);
        return (true);
    }
}