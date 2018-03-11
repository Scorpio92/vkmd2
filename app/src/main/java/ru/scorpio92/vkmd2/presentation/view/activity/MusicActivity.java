package ru.scorpio92.vkmd2.presentation.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.App;
import ru.scorpio92.vkmd2.BuildConfig;
import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.Track;
import ru.scorpio92.vkmd2.data.repository.db.TrackProvider;
import ru.scorpio92.vkmd2.presentation.presenter.MusicPresenter;
import ru.scorpio92.vkmd2.presentation.presenter.base.IMusicPresenter;
import ru.scorpio92.vkmd2.presentation.view.activity.base.AbstractActivity;
import ru.scorpio92.vkmd2.presentation.view.activity.base.IMusicActivity;
import ru.scorpio92.vkmd2.presentation.view.adapter.SpacesItemDecoration;
import ru.scorpio92.vkmd2.presentation.view.adapter.TrackListAdapter;
import ru.scorpio92.vkmd2.service.AudioService;
import ru.scorpio92.vkmd2.service.DownloadService;
import ru.scorpio92.vkmd2.service.SyncService;
import ru.scorpio92.vkmd2.tools.DateUtils;
import ru.scorpio92.vkmd2.tools.Dialog;
import ru.scorpio92.vkmd2.tools.LocalStorage;
import ru.scorpio92.vkmd2.tools.Logger;

import static ru.scorpio92.vkmd2.Constants.AUTHOR_URL;

public class MusicActivity extends AbstractActivity<IMusicPresenter> implements IMusicActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private LinearLayoutCompat toolbarHeader;

    private SearchView searchView;
    private LinearLayoutCompat additionalSearchPanel;
    private AppCompatCheckBox onlineSearch;

    private ProgressBar progress, onlineSearchProgress;
    private LinearLayoutCompat trackListContainer, footer;
    private BottomSheetBehavior bottomSheetBehavior;
    private ImageView trackImage;
    private RecyclerView trackListView;
    private FloatingActionButton fab;
    private TrackListAdapter trackListAdapter;
    private AppCompatTextView selectedDesc, trackName, trackArtist, currentTime, durationTime;
    private ImageButton selectAll, unselectAll, download, loopBtn, prevBtn, playBtn, pauseBtn, nextBtn, randomBtn;
    private volatile ProgressBar pickerProgress, prepareProgress;
    private AppCompatSeekBar playProgress;

    private String provider = TrackProvider.PROVIDER.ACCOUNT_TABLE.name();

    private boolean offlineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        registerPresenter(new MusicPresenter(this));

        initUI();

        LocalBroadcastManager.getInstance(this).registerReceiver(audioServiceEventsReceiver, new IntentFilter(AudioService.SERVICE_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadServiceEventsReceiver, new IntentFilter(DownloadService.SERVICE_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver(syncServiceEventsReceiver, new IntentFilter(SyncService.SERVICE_BROADCAST));


        getPresenter().getTrackList();
        startService(new Intent(this, SyncService.class)
                .putExtra(SyncService.SERVICE_ACTION, SyncService.ACTION.START.name())
                .putExtra(SyncService.IS_AUTO_SYNC, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackListView.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioServiceEventsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(audioServiceEventsReceiver);
            audioServiceEventsReceiver = null;
        }
        if (downloadServiceEventsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadServiceEventsReceiver);
            downloadServiceEventsReceiver = null;
        }
        if (syncServiceEventsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(syncServiceEventsReceiver);
            syncServiceEventsReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (toggle.isDrawerIndicatorEnabled()) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else
                showExitDialog();
        }
    }

    @Override
    public Context getViewContext() {
        return MusicActivity.this;
    }

    @Override
    public void showProgress(boolean show) {
        trackListContainer.setVisibility(View.GONE);
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showTrackList(List<Track> trackList) {
        trackListContainer.setVisibility(View.VISIBLE);
        trackListAdapter.renderTrackList(trackList);

        startService(new Intent(MusicActivity.this, AudioService.class)
                .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.GET_INFO.name())
        );
    }

    @Override
    public void showMusicFooterInfo(String artist, String name, int duration, String imageUrl) {
        trackArtist.setText(artist);
        trackName.setText(name);
        playProgress.setProgress(0);
        playProgress.setMax(duration * 1000);
        currentTime.setText(DateUtils.getHumanTimeFromMilliseconds(0));
        durationTime.setText(DateUtils.getHumanTimeFromMilliseconds(duration * 1000));
        footer.setVisibility(View.VISIBLE);
        trackListView.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_80));
        fab.setVisibility(View.VISIBLE);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(this)
                    .load(imageUrl)
                    .into(trackImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            trackImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        @Override
                        public void onError() {
                            trackImage.setScaleType(ImageView.ScaleType.CENTER);
                            trackImage.setImageResource(R.mipmap.note);
                        }
                    });
        } else {
            trackImage.setScaleType(ImageView.ScaleType.CENTER);
            trackImage.setImageResource(R.mipmap.note);
        }
    }

    @Override
    public void showCurrentPlayProgress(int progress) {
        playProgress.setProgress(progress);
        currentTime.setText(DateUtils.getHumanTimeFromMilliseconds(progress));
    }

    @Override
    public void showPrepareForPlay(boolean show) {
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.GONE);
        prepareProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        //pickerProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            pickerProgress.setVisibility(View.VISIBLE);
        } else {
            pickerProgress.postDelayed(() -> pickerProgress.setVisibility(View.GONE), 500);
        }
    }

    @Override
    public void showPauseButton() {
        pauseBtn.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.GONE);
        fab.setImageResource(R.mipmap.pause);
    }

    @Override
    public void showPlayButton() {
        pauseBtn.setVisibility(View.GONE);
        playBtn.setVisibility(View.VISIBLE);
        fab.setImageResource(R.mipmap.play);
    }

    @Override
    public void showPrepareForDownload() {
        showToast("Загрузка скоро начнётся...");
    }

    @Override
    public void startDownloadService() {
        startService(new Intent(this, DownloadService.class)
                .putExtra(DownloadService.SERVICE_ACTION, DownloadService.ACTION.START_DOWNLOAD.name()));
    }

    @Override
    public void showOnlineSearchProgress(boolean show) {
        onlineSearchProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
        toolbarHeader = findViewById(R.id.header);

        initDrawerLayout();

        additionalSearchPanel = findViewById(R.id.search_additional_panel);
        onlineSearch = findViewById(R.id.onlineSearch);
        searchView = findViewById(R.id.search);
        searchView.setQueryHint("Поиск по аудиозаписям...");
        searchView.setIconifiedByDefault(true);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!offlineMode)
                    additionalSearchPanel.setVisibility((!newText.isEmpty()) ? View.VISIBLE : View.GONE);

                if (newText.isEmpty()) {
                    provider = TrackProvider.PROVIDER.ACCOUNT_TABLE.name();
                } else {
                    provider = onlineSearch.isChecked() ? TrackProvider.PROVIDER.ONLINE_SEARCH_TABLE.name() : TrackProvider.PROVIDER.OFFLINE_SEARCH_TABLE.name();
                }

                if (onlineSearch.isChecked()) {
                    getPresenter().getOnlineTracks(newText);
                } else {
                    if (newText.isEmpty()) {
                        if (offlineMode)
                            getPresenter().getSavedTrackList();
                        else
                            getPresenter().getTrackList();
                    } else {
                        trackListAdapter.filter(newText);
                    }
                }

                return false;
            }
        });
        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButton.setOnClickListener(v -> {
            provider = TrackProvider.PROVIDER.ACCOUNT_TABLE.name();
            searchView.setQuery("", false);
            onlineSearch.setChecked(false);
            onlineSearch.requestLayout();
            additionalSearchPanel.setVisibility(View.GONE);
            hideKeyboard();

            /*if (onlineSearch.isChecked()) {
                getPresenter().getTrackList();
            } else {
                trackListAdapter.resetFilter();
            }*/
            if (offlineMode)
                getPresenter().getSavedTrackList();
            else
                getPresenter().getTrackList();
        });

        /*AppCompatCheckBox filterByArtist = findViewById(R.id.filterByArtist);
        AppCompatCheckBox filterByTrackName = findViewById(R.id.filterByTrackName);
        filterByArtist.setOnClickListener(v -> {
            trackListAdapter.setFilterByArtist(filterByArtist.isChecked());
            trackListAdapter.filter(searchView.getQuery());
        });
        filterByTrackName.setOnClickListener(v -> {
            trackListAdapter.setFilterByTrackName(filterByTrackName.isChecked());
            trackListAdapter.filter(searchView.getQuery());
        });*/
        onlineSearch.setOnClickListener(v -> {
            provider = onlineSearch.isChecked() ? TrackProvider.PROVIDER.ONLINE_SEARCH_TABLE.name() : TrackProvider.PROVIDER.OFFLINE_SEARCH_TABLE.name();
            trackListAdapter.setOnlineSearch(onlineSearch.isChecked());
            /*if (!onlineSearch.isChecked()) {
                getPresenter().getTrackList();
            }*/
            if (onlineSearch.isChecked()) {
                getPresenter().getOnlineTracks(searchView.getQuery());
            } else {
                trackListAdapter.filter(searchView.getQuery());
            }
        });

        onlineSearchProgress = findViewById(R.id.onlineSearchProgress);

        selectedDesc = findViewById(R.id.selectedDesc);
        selectAll = findViewById(R.id.selectAll);
        unselectAll = findViewById(R.id.unselectAll);
        selectAll.setOnClickListener(v -> {
            if (trackListAdapter.getItemCount() == 0) {
                showToast("А выбирать то нечего!");
            } else {
                selectAll.setVisibility(View.GONE);
                unselectAll.setVisibility(View.VISIBLE);
                trackListAdapter.selectAll();
                selectedDesc.setVisibility(View.VISIBLE);
                selectedDesc.setText(String.format("Выбрано: %d", trackListAdapter.getSelectedTracks().size()));
                download.setVisibility(View.VISIBLE);
            }
        });
        unselectAll.setOnClickListener(v -> {
            hideAdditionalButtonsInToolbar();
            trackListAdapter.uncheckAll();
        });
        download = findViewById(R.id.download);
        download.setOnClickListener(v -> {
            if (trackListAdapter.checkSelectedTracksIsEmpty()) {
                showToast("А качать то нечего!");
                hideAdditionalButtonsInToolbar();
                trackListAdapter.uncheckAll();
            } else {
                AlertDialog.Builder builder = Dialog.getAlertDialogBuilder("Внимание", "Вы действительно хотите всё это скачать? Оно Вам надо?", MusicActivity.this);
                builder.setCancelable(false);
                builder.setNegativeButton("Нет", (dialog, which) -> {
                    hideAdditionalButtonsInToolbar();
                    trackListAdapter.uncheckAll();
                    dialog.dismiss();
                });
                builder.setPositiveButton("Да!", (dialog, which) -> {
                    getPresenter().sendTracksForDownload(trackListAdapter.getSelectedTracks());
                    hideAdditionalButtonsInToolbar();
                    trackListAdapter.uncheckAll();
                    dialog.dismiss();
                });
                builder.show();
            }
        });

        progress = findViewById(R.id.progress);
        progress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);

        trackListContainer = findViewById(R.id.trackListContainer);

        trackListAdapter = new TrackListAdapter(this, new ArrayList<>(), trackListOnItemClickListener);
        trackListView = findViewById(R.id.trackList);
        trackListView.addItemDecoration(new SpacesItemDecoration(0));
        trackListView.setHasFixedSize(true);
        trackListView.setNestedScrollingEnabled(false);
        trackListView.setLayoutManager(new LinearLayoutManager(this));
        trackListView.setItemAnimator(new DefaultItemAnimator());
        trackListView.setAdapter(trackListAdapter);

        footer = findViewById(R.id.footer);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startService(new Intent(MusicActivity.this, AudioService.class)
                .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PLAY_OR_PAUSE.name())
        ));

        bottomSheetBehavior = BottomSheetBehavior.from(footer);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        LinearLayoutCompat picker = findViewById(R.id.picker);
        picker.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        toolbarHeader.setVisibility(View.GONE);
                        break;
                    default:
                        toolbarHeader.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
            }
        });

        trackImage = findViewById(R.id.image);

        trackName = findViewById(R.id.trackName);
        trackArtist = findViewById(R.id.artist);

        playProgress = findViewById(R.id.playProgress);
        playProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startService(new Intent(MusicActivity.this, AudioService.class)
                        .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.SEEK_TO.name())
                        .putExtra(AudioService.AUDIO_SEEK_PARAM, seekBar.getProgress()));
            }
        });

        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);

        loopBtn = findViewById(R.id.loop);
        playBtn = findViewById(R.id.play);
        pauseBtn = findViewById(R.id.pause);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.prev);
        randomBtn = findViewById(R.id.random);

        pickerProgress = findViewById(R.id.pickerProgress);
        pickerProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorRed), PorterDuff.Mode.MULTIPLY);

        prepareProgress = findViewById(R.id.prepareProgress);
        prepareProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);

        loopBtn.setOnClickListener(v -> {
            startService(new Intent(MusicActivity.this, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.LOOP_FEATURE.name())
            );
        });

        playBtn.setOnClickListener(v -> {
            showPauseButton();
            startService(new Intent(MusicActivity.this, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PLAY_OR_PAUSE.name())
            );
        });
        pauseBtn.setOnClickListener(v -> {
            showPlayButton();
            startService(new Intent(MusicActivity.this, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PAUSE.name())
            );
        });
        nextBtn.setOnClickListener(v -> startService(new Intent(MusicActivity.this, AudioService.class)
                .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.NEXT.name())
        ));
        prevBtn.setOnClickListener(v -> startService(new Intent(MusicActivity.this, AudioService.class)
                .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PREV.name())
        ));
        randomBtn.setOnClickListener(v -> startService(new Intent(MusicActivity.this, AudioService.class)
                .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.RANDOM_FEATURE.name())
        ));
    }

    private void hideAdditionalButtonsInToolbar() {
        selectAll.setVisibility(View.VISIBLE);
        unselectAll.setVisibility(View.GONE);
        selectedDesc.setVisibility(View.GONE);
        download.setVisibility(View.GONE);
    }

    private void initDrawerLayout() {
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black)));
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_offline_mode:
                    startService(new Intent(MusicActivity.this, AudioService.class)
                            .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.STOP.name())
                    );

                    AppCompatCheckBox checkBox = item.getActionView().findViewById(R.id.checkbox);
                    checkBox.setOnClickListener(v -> {
                        offlineMode = checkBox.isChecked();
                        onOfflineModeCheck();
                    });
                    checkBox.setChecked(!checkBox.isChecked());
                    offlineMode = checkBox.isChecked();
                    onOfflineModeCheck();
                    break;
                case R.id.menu_sync:
                    AlertDialog.Builder builder = Dialog.getAlertDialogBuilder("Внимание", "Синхронизировать локальную библиотеку аудио с акаунтом ВК ?", MusicActivity.this);
                    builder.setCancelable(false);
                    builder.setNegativeButton("Нет", (dialog, which) -> dialog.dismiss());
                    builder.setPositiveButton("Да", (dialog, which) -> {
                        startActivity(new Intent(MusicActivity.this, AuthActivity.class).putExtra(AuthActivity.FORCE_SYNC_KEY, true));
                        finish();
                    });
                    builder.show();
                    break;
                case R.id.menu_download_man:
                    startActivity(new Intent(MusicActivity.this, DownloadManagerActivity.class));
                    break;
                case R.id.menu_settings:
                    startActivity(new Intent(MusicActivity.this, SettingsActivity.class));
                    break;
                case R.id.menu_about:
                    AlertDialog.Builder builderID = Dialog.getAlertDialogBuilder("О программе", "Данное приложение для прослушивания и скачивания музыки с ресурса VK.com создано на добровольных началах для простого рабочего класса на зло жлобистой буржуйской компании Mail.Ru Group.\nИспользование приложения в коммерческих целях запрещено.\nПо вопросам и предложениям обращаться по контактам указанным на сайте автора.\nПриятного пользования!", MusicActivity.this);
                    builderID.setCancelable(false);
                    builderID.setNegativeButton("ОК", (dialog, which) -> dialog.dismiss());
                    builderID.setPositiveButton("Об авторе", (dialog, which) -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(AUTHOR_URL));
                        startActivity(i);
                        dialog.dismiss();
                    });
                    builderID.show();
                    break;
                case R.id.menu_faq:
                    startActivity(new Intent(MusicActivity.this, FaqActivity.class));
                    break;
                case R.id.menu_exit:
                    showExitDialog();
                    break;

            }
            drawer.closeDrawer(GravityCompat.START);

            return true;
        });

        AppCompatTextView title = navigationView.getHeaderView(0).findViewById(R.id.title);
        title.setText(getString(R.string.title).concat(" v").concat(BuildConfig.VERSION_NAME));
    }

    private void onOfflineModeCheck() {
        //т.к. аудиосервис перезапускается, настройки лупа и рандома сбрасываются, то надо поменять их цвет на дефолтный
        loopBtn.setColorFilter(getResources().getColor(android.R.color.black));
        randomBtn.setColorFilter(getResources().getColor(android.R.color.black));

        if (offlineMode) {
            toolbarHeader.setVisibility(View.GONE);
            onlineSearch.setChecked(false);
            provider = TrackProvider.PROVIDER.SAVED_TABLE.name();
            getPresenter().getSavedTrackList();
        } else {
            toolbarHeader.setVisibility(View.VISIBLE);
            onlineSearch.setChecked(false);
            provider = TrackProvider.PROVIDER.ACCOUNT_TABLE.name();
            getPresenter().getTrackList();
        }
    }

    private void checkFirstRun() {
        try {
            if (!LocalStorage.fileExist(this, LocalStorage.IS_NOT_FIRST_RUN)) {
                Dialog.getAlertDialogBuilder("Добро пожаловать!", "Это первый запуск приложения и вы получили свои первые 200 аудиозаписей!\nЧтобы получить больше, выполните ручную синхронизацию (Меню -> Синхронизация) с укзанием примерного кол-ва аудиозаписей на вашей странице.", this)
                        .setPositiveButton("OK", (dialog, which) -> {
                            try {
                                LocalStorage.setDataInFile(MusicActivity.this, LocalStorage.IS_NOT_FIRST_RUN, "");
                                dialog.dismiss();
                            } catch (Exception e) {
                                Logger.error(e);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = Dialog.getAlertDialogBuilder("Внимание", "Закрыть приложение ?", MusicActivity.this);
        builder.setCancelable(false);
        builder.setNegativeButton("Нет", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Да", (dialog, which) -> {
            startService(new Intent(MusicActivity.this, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.STOP.name())
            );
            startService(new Intent(MusicActivity.this, SyncService.class)
                    .putExtra(SyncService.SERVICE_ACTION, SyncService.ACTION.STOP.name())
            );
            App.finish();
            finish();
        });
        builder.show();
    }

    private TrackListAdapter.Listener trackListOnItemClickListener = new TrackListAdapter.Listener() {
        @Override
        public void onTrackClick(String trackId, String artist, String name, int duration, String imageUrl) {
            showMusicFooterInfo(artist, name, duration, imageUrl);
            showPauseButton();
            startService(new Intent(MusicActivity.this, AudioService.class)
                    .putExtra(AudioService.SERVICE_ACTION, AudioService.ACTION.PLAY.name())
                    .putExtra(AudioService.AUDIO_PROVIDER, provider)
                    .putExtra(AudioService.AUDIO_TRACK_ID_PARAM, trackId));
        }

        @Override
        public void onCheckChanged() {
            if (trackListAdapter.getSelectedTracks().size() > 0) {
                selectAll.setVisibility(View.GONE);
                unselectAll.setVisibility(View.VISIBLE);
                selectedDesc.setVisibility(View.VISIBLE);
                selectedDesc.setText(String.format("Выбрано: %d", trackListAdapter.getSelectedTracks().size()));
                download.setVisibility(View.VISIBLE);
            } else {
                hideAdditionalButtonsInToolbar();
            }
        }

        @Override
        public void onFilterComplete(List<String> tracksId) {
            getPresenter().saveOfflineSearch(tracksId);
        }
    };

    private BroadcastReceiver audioServiceEventsReceiver = new BroadcastReceiver() {

        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String eventStr = bundle.getString(AudioService.SERVICE_EVENT);
                if (eventStr != null) {
                    switch (Enum.valueOf(AudioService.EVENT.class, eventStr)) {
                        case PROVIDE_INFO:
                            showPrepareForPlay(false);
                            showMusicFooterInfo(
                                    bundle.getString(AudioService.AUDIO_TRACK_ARTIST_PARAM),
                                    bundle.getString(AudioService.AUDIO_TRACK_NAME_PARAM),
                                    bundle.getInt(AudioService.AUDIO_TRACK_DURATION_PARAM),
                                    bundle.getString(AudioService.AUDIO_TRACK_IMAGE_URL_PARAM)
                            );
                            showCurrentPlayProgress(bundle.getInt(AudioService.AUDIO_TRACK_PROGRESS_PARAM, 0));
                            if (bundle.getBoolean(AudioService.AUDIO_TRACK_IS_PLAY_PARAM, true)) {
                                showPauseButton();
                            } else {
                                showPlayButton();
                            }
                            trackListAdapter.showCurrentTrackIsPlayed(bundle.getString(AudioService.AUDIO_TRACK_ID_PARAM));
                            //trackListView.smoothScrollToPosition(bundle.getInt(AudioService.AUDIO_TRACK_POSITION_PARAM) - 1);
                            break;
                        case LOOP_ENABLED:
                            showToast("Повтор композиции включен");
                            loopBtn.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                            break;
                        case LOOP_DISABLED:
                            showToast("Повтор композиции выключен");
                            loopBtn.setColorFilter(getResources().getColor(android.R.color.black));
                            break;
                        case RANDOM_ENABLED:
                            showToast("Режим случайного воспроизведения включен");
                            randomBtn.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                            break;
                        case RANDOM_DISABLED:
                            showToast("Режим случайного воспроизведения выключен");
                            randomBtn.setColorFilter(getResources().getColor(android.R.color.black));
                            break;
                        case PREPARE_FOR_PLAY:
                            showPrepareForPlay(true);
                            showMusicFooterInfo(
                                    bundle.getString(AudioService.AUDIO_TRACK_ARTIST_PARAM),
                                    bundle.getString(AudioService.AUDIO_TRACK_NAME_PARAM),
                                    bundle.getInt(AudioService.AUDIO_TRACK_DURATION_PARAM),
                                    bundle.getString(AudioService.AUDIO_TRACK_IMAGE_URL_PARAM)
                            );
                            trackListAdapter.showCurrentTrackIsPlayed(bundle.getString(AudioService.AUDIO_TRACK_ID_PARAM));
                            //trackListView.smoothScrollToPosition(bundle.getInt(AudioService.AUDIO_TRACK_POSITION_PARAM) - 1);
                            break;
                        case START_PLAY:
                            showPrepareForPlay(false);
                            showPauseButton();
                            break;
                        case PROGRESS_UPDATE:
                            showCurrentPlayProgress(bundle.getInt(AudioService.AUDIO_TRACK_PROGRESS_PARAM, 0));
                            break;
                        case PAUSE:
                            showPlayButton();
                            break;
                        case STOP_SERVICE:
                            footer.setVisibility(View.GONE);
                            trackListView.setPadding(0, 0, 0, 0);
                            fab.setVisibility(View.GONE);
                            trackListAdapter.showNoneTrackIsPlayed();
                            break;
                        case ERROR:
                            showPrepareForPlay(false);
                            showPlayButton();
                            showToast("Произошла ошибка при воспроизведении");
                    }
                }
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
                        case TRACK_DOWNLOAD_COMPLETE:
                            trackListAdapter.renderTrack(bundle.getString(DownloadService.AUDIO_TRACK_ID_PARAM));
                            break;
                    }
                }
            }
        }
    };

    private BroadcastReceiver syncServiceEventsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String eventStr = bundle.getString(SyncService.SERVICE_EVENT);
                if (eventStr != null) {
                    switch (Enum.valueOf(SyncService.EVENT.class, eventStr)) {
                        case SYNC_START:
                            showToast("Синхронизация аудио запущена");
                            break;
                        case SYNC_FINISH:
                            showToast("Синхронизация аудио закончена");
                            getPresenter().getTrackList();
                            break;
                        case SYNC_WAS_COMPLETED:
                            //showToast("Синхронизация аудио была выполнена ранее");
                            getPresenter().getTrackList();
                            checkFirstRun();
                            break;
                        case SYNC_ERROR:
                            showToast("Ошибка синхронизации аудио");
                            break;
                    }
                }
            }
        }
    };
}
