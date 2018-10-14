package ru.scorpio92.vkmd2.presentation.auth.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomWebView extends WebView {

    public void registerWebViewClientCallback(CustomWebViewClient.WebViewClientCallback callback) {
        this.setWebViewClient(new CustomWebViewClient(callback));
    }

    public void registerMyWebChromeClientCallback(CustomWebChromeClient.MyWebChromeClientCallback callback) {
        this.setWebChromeClient(new CustomWebChromeClient(callback));
    }

    public CustomWebView(Context context) {
        super(context);
        initView();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    public void initView() {

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setLoadsImagesAutomatically(false);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.getSettings().setDefaultTextEncodingName("utf-8");
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        setDesktopMode(false);
    }

    public void setDesktopMode(boolean enabled) {
        String newUserAgent = this.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = this.getSettings().getUserAgentString();
                String androidOSString = this.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = this.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        this.getSettings().setUserAgentString(newUserAgent);
        this.getSettings().setUseWideViewPort(enabled);
        this.getSettings().setLoadWithOverviewMode(enabled);
    }
}
