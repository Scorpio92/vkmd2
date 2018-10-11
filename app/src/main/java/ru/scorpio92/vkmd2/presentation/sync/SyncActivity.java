package ru.scorpio92.vkmd2.presentation.sync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.di.PresenterInjection;
import ru.scorpio92.vkmd2.presentation.base.BaseActivity;
import ru.scorpio92.vkmd2.presentation.old.view.activity.MusicActivity;

import static ru.scorpio92.vkmd2.BuildConfig.GET_AUDIO_OFFSET;
import static ru.scorpio92.vkmd2.tools.ViewUtils.hideSoftKeyboard;

public class SyncActivity extends BaseActivity<IContract.Presenter> implements IContract.View {

    private LinearLayoutCompat mainContainer, errorContainer, progressContainer;
    private AppCompatEditText countEt;

    @Nullable
    @Override
    protected IContract.Presenter bindPresenter() {
        return PresenterInjection.provideSyncPresenter(this);
    }

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.activity_sync;
    }

    @Override
    protected void initUI() {
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

    @Override
    public void showProgress() {
        mainContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressContainer.setVisibility(View.GONE);
    }

    @Override
    public void onError(@NonNull String error) {
        errorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finishApp();
    }

    @Override
    public void showMusicActivity() {
        startActivity(new Intent(this, MusicActivity.class));
        finish();
    }

    private void getAudio() {
        if (checkPresenterState()) {
            hideSoftKeyboard(this);
            if (countEt.getText().toString().trim().isEmpty())
                getPresenter().synchronize(GET_AUDIO_OFFSET);
            else
                getPresenter().synchronize(Integer.valueOf(countEt.getText().toString().trim()));
        }
    }
}
