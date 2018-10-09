package ru.scorpio92.vkmd2.presentation.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ProgressBar;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.base.BaseActivity;
import ru.scorpio92.vkmd2.presentation.old.view.activity.MusicActivity;
import ru.scorpio92.vkmd2.presentation.old.view.activity.SyncActivity;
import ru.scorpio92.vkmd2.presentation.old.view.webview.CustomWebView;
import ru.scorpio92.vkmd2.presentation.old.view.webview.CustomWebViewClient;
import ru.scorpio92.vkmd2.tools.Dialog;

import static ru.scorpio92.vkmd2.BuildConfig.BASE_URL;


public class AuthActivity extends BaseActivity<IContract.Presenter> implements IContract.View {

    private CustomWebView webView;
    private ProgressBar progress;
    private LinearLayoutCompat errorContainer;

    @Override
    protected boolean retryAppInitOnCreate() {
        return true;
    }

    @Nullable
    @Override
    protected IContract.Presenter bindPresenter() {
        return new AuthPresenter(this);
    }

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.activity_auth;
    }

    @Override
    protected void initUI() {
        webView = findViewById(R.id.webView);
        webView.registerWebViewClientCallback(webViewClientCallback);

        progress = findViewById(R.id.progress);
        progress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(
                this, R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);

        errorContainer = findViewById(R.id.errorContainer);

        findViewById(R.id.retryBtn).setOnClickListener(v -> {
            if (checkPresenterState())
                getPresenter().onPostCreate();
        });

        webView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onError(@NonNull String error) {
        webView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionNotGranted() {
        Dialog.getAlertDialogBuilder(null, getString(R.string.need_permissions), this)
                .setPositiveButton(getString(R.string.retry), (dialog, which) -> {
                    dialog.dismiss();
                    if (checkPresenterState())
                        getPresenter().onPostCreate();
                })
                .setNegativeButton(getString(R.string.dialog_close), (dialog, which) -> {
                    dialog.dismiss();
                    finishApp();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void loadVkPage() {
        webView.loadUrl(BASE_URL);
    }

    @Override
    public void showAttentionDialog() {
        Dialog.getAlertDialogBuilder(getString(R.string.dialog_login_title), getString(R.string.dialog_login), this)
                .setPositiveButton(getString(R.string.dialog_continue), (dialog, which) -> {
                    dialog.dismiss();
                    if (checkPresenterState())
                        getPresenter().onUserReadAttention();
                })
                .setNegativeButton(getString(R.string.dialog_close), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finishApp();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void showVkPage() {
        webView.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
    }

    @Override
    public void showSyncActivity() {
        startActivity(new Intent(this, SyncActivity.class));
        finish();
    }

    @Override
    public void showMusicActivity() {
        startActivity(new Intent(this, MusicActivity.class));
        finish();
    }

    private CustomWebViewClient.WebViewClientCallback webViewClientCallback = new CustomWebViewClient.WebViewClientCallback() {
        @Override
        public void onAuthPageLoaded() {
            if (checkPresenterState())
                getPresenter().onAuthPageLoaded();
        }

        @Override
        public void onPageBeginLoading() {
            if (checkPresenterState())
                getPresenter().onPageBeginLoading();
        }

        @Override
        public void onCookieReady(String cookie) {
            if (checkPresenterState())
                getPresenter().onCookieReady(cookie);
        }

        @Override
        public void onError() {
            if (checkPresenterState())
                getPresenter().onError();
        }
    };
}
