package ru.scorpio92.vkmd2.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.presentation.presenter.DownloadManagerPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.IDownloadManagerPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.AbstractActivity;
import ru.scorpio92.vkmd2.presentation.view.activity.base.IDownloadManagerActivity;
import ru.scorpio92.vkmd2.presentation.view.adapter.DownloadListAdapter;
import ru.scorpio92.vkmd2.presentation.view.adapter.SpacesItemDecoration;
import ru.scorpio92.vkmd2.service.DownloadService;


public class DownloadManagerActivity extends AbstractActivity<IDownloadManagerPresenter> implements IDownloadManagerActivity {

    private ProgressBar progress;
    private LinearLayoutCompat downloadListContainer;
    private DownloadListAdapter downloadListAdapter;
    private AppCompatTextView selectedDesc;
    private ImageButton start, pause, retry, removeDownloads, selectAll, unselectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        registerPresenter(new DownloadManagerPresenter(this));

        initUI();

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadServiceEventsReceiver, new IntentFilter(DownloadService.SERVICE_BROADCAST));

        startService(new Intent(DownloadManagerActivity.this, DownloadService.class)
                        .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.GET_INFO.name()));

        getPresenter().getDownloadList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (downloadServiceEventsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadServiceEventsReceiver);
            downloadServiceEventsReceiver = null;
        }
    }

    @Override
    public void renderDownloadList(List<Track> trackList) {
        downloadListContainer.setVisibility(View.VISIBLE);
        downloadListAdapter.renderDownloadList(trackList);
    }

    @Override
    public void onSuccessRemoveFromDownloadList() {
        downloadListAdapter.removeSelectedTracksFromList();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initUI() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        progress = findViewById(R.id.progress);
        progress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);

        downloadListContainer = findViewById(R.id.downloadListContainer);

        downloadListAdapter = new DownloadListAdapter(new ArrayList<>(), downloadListAdapterListener);
        RecyclerView downloadList = findViewById(R.id.downloadList);
        downloadList.addItemDecoration(new SpacesItemDecoration(0));
        downloadList.setHasFixedSize(true);
        downloadList.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        downloadList.setLayoutManager(manager);
        downloadList.setItemAnimator(new DefaultItemAnimator());
        downloadList.setAdapter(downloadListAdapter);

        selectedDesc = findViewById(R.id.selectedDesc);
        selectAll = findViewById(R.id.selectAll);
        selectAll.setOnClickListener(v -> {
            if(downloadListAdapter.getItemCount() == 0) {
                showToast(getString(R.string.error_nothing_select));
            } else {
                downloadListAdapter.selectAll();
                showHideAdditionalButtonsInToolbar(true);
            }
        });
        unselectAll = findViewById(R.id.unselectAll);
        unselectAll.setOnClickListener(v -> {
            downloadListAdapter.uncheckAll();
            showHideAdditionalButtonsInToolbar(false);
        });
        start = findViewById(R.id.start);
        start.setOnClickListener(v -> {
            startService(new Intent(DownloadManagerActivity.this, DownloadService.class)
                    .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.START_PAUSE.name())
            );
        });
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(v -> {
            startService(new Intent(DownloadManagerActivity.this, DownloadService.class)
                    .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.START_PAUSE.name())
            );
        });
        retry = findViewById(R.id.retry);
        retry.setOnClickListener(v -> {
            showHideAdditionalButtonsInToolbar(false);
            getPresenter().sendTracksForDownload(downloadListAdapter.getSelectedTracks());
            downloadListAdapter.markSelectedTracksForRedownload();
            downloadListAdapter.uncheckAll();
        });
        removeDownloads = findViewById(R.id.removeDownloads);
        removeDownloads.setOnClickListener(v -> {
            showHideAdditionalButtonsInToolbar(false);
            getPresenter().removeTracksFromDownloadList(downloadListAdapter.getSelectedTracks());
        });
    }

    private void showStartPauseButtons(boolean showStart) {
        if (showStart) {
            start.setVisibility(View.VISIBLE);
            pause.setVisibility(View.GONE);
        } else {
            start.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    private void showHideAdditionalButtonsInToolbar(boolean show) {
        if(show) {
            selectAll.setVisibility(View.GONE);
            unselectAll.setVisibility(View.VISIBLE);
            selectedDesc.setVisibility(View.VISIBLE);
            selectedDesc.setText(String.format(getString(R.string.selected) + "%d", downloadListAdapter.getSelectedTracks().size()));
            retry.setVisibility(View.VISIBLE);
            removeDownloads.setVisibility(View.VISIBLE);
        } else {
            selectAll.setVisibility(View.VISIBLE);
            unselectAll.setVisibility(View.GONE);
            selectedDesc.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            removeDownloads.setVisibility(View.GONE);
        }
    }

    private DownloadListAdapter.Listener downloadListAdapterListener = new DownloadListAdapter.Listener() {
        @Override
        public void onCheckChanged() {
            if (downloadListAdapter.getSelectedTracks().size() > 0) {
                showHideAdditionalButtonsInToolbar(true);
            } else {
                showHideAdditionalButtonsInToolbar(false);
            }
        }
    };

    private BroadcastReceiver downloadServiceEventsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String eventStr = bundle.getString(DownloadService.SERVICE_EVENT);
                if (eventStr != null) {
                    switch (Enum.valueOf(DownloadService.EVENT.class, eventStr)) {
                        case GENERAL_START_DOWNLOAD:
                            showToast(getString(R.string.download_started));
                            showStartPauseButtons(false);
                            break;
                        case GENERAL_DOWNLOAD_COMPLETE:
                            showToast(getString(R.string.download_completed));
                            showStartPauseButtons(true);
                            break;
                        case DOWNLOAD_PROGRESS_UPDATE:
                            showStartPauseButtons(false);
                            downloadListAdapter.onTrackDownloadProgress(bundle.getString(DownloadService.AUDIO_TRACK_ID_PARAM), bundle.getInt(DownloadService.AUDIO_TRACK_PROGRESS_PARAM));
                            break;
                        case TRACK_DOWNLOAD_COMPLETE:
                            downloadListAdapter.onTrackDownloaded(bundle.getString(DownloadService.AUDIO_TRACK_ID_PARAM));
                            break;
                        case TRACK_DOWNLOAD_ERROR:
                            downloadListAdapter.onTrackDownloadError(bundle.getString(DownloadService.AUDIO_TRACK_ID_PARAM));
                            break;
                        case GENERAL_DOWNLOAD_ERROR:
                            showStartPauseButtons(true);
                            showToast(getString(R.string.error_download));
                            break;
                        case NOTHING_DOWNLOAD:
                            showStartPauseButtons(true);
                            showToast(getString(R.string.error_nothing_download));
                            break;
                        case DOWNLOAD_ACTIVE:
                            showStartPauseButtons(false);
                            break;
                        case DOWNLOAD_INACTIVE:
                            showStartPauseButtons(true);
                            break;
                    }
                }
            }
        }
    };
}
