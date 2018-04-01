package ru.scorpio92.vkmd2.presentation.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import ru.scorpio92.vkmd2.App;
import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.presenter.SyncPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.ISyncPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.AbstractActivity;
import ru.scorpio92.vkmd2.presentation.view.activity.base.ISyncActivity;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;


public class SyncActivity extends AbstractActivity<ISyncPresenter> implements ISyncActivity {

    public final static String AUTO_RUN_KEY = "auto_run";

    private LinearLayoutCompat mainContainer, errorContainer, progressContainer;
    AppCompatEditText countEt;

    private String cookie = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        initUI();
        registerPresenter(new SyncPresenter(this));

        try {
            cookie = LocalStorage.getDataFromFile(this, LocalStorage.COOKIE_STORAGE);
        } catch (Exception e) {
            Logger.error(e);
        }

        if(getIntent().getBooleanExtra(AUTO_RUN_KEY, false)) {
            getAudio();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.finish();
    }

    @Override
    public void showMusicActivity() {
        startActivity(new Intent(this, MusicActivity.class));
        finish();
    }

    @Override
    public void onError() {
        errorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showProgress(boolean show) {
        mainContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initUI() {
        mainContainer = findViewById(R.id.mainContainer);

        countEt = findViewById(R.id.countEt);
        countEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getAudio();
            }
            return false;
        });

        findViewById(R.id.goBtn).setOnClickListener(v -> getAudio());

        progressContainer = findViewById(R.id.progressContainer);

        errorContainer = findViewById(R.id.errorContainer);
        findViewById(R.id.retryBtn).setOnClickListener(v -> getAudio());

        mainContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        progressContainer.setVisibility(View.GONE);
    }

    private void getAudio() {
        hideKeyboard();
        if (countEt.getText().toString().trim().isEmpty())
            getPresenter().parsePageSourceCode(cookie, GET_AUDIO_OFFSET);
        else
            getPresenter().parsePageSourceCode(cookie, Integer.valueOf(countEt.getText().toString().trim()));
    }
}
