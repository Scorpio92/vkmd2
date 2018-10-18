package ru.scorpio92.vkmd2.presentation.main.activity;

import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

import ru.scorpio92.vkmd2.BuildConfig;
import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.base.BaseActivity;
import ru.scorpio92.vkmd2.presentation.base.IFragmentListener;
import ru.scorpio92.vkmd2.presentation.main.fragment.player.PlayerFragment;
import ru.scorpio92.vkmd2.tools.Dialog;
import ru.scorpio92.vkmd2.tools.Logger;

public class MainActivity extends BaseActivity implements IFragmentListener {

    public static class FragmentResult {
        public static final int TRACK_LIST_UPDATE = 0;
        public static final int TRACK_LIST_LOADING_ERROR = 1;
    }

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;

    /**
     * Первый таб (плеер) был проинициализирован
     */
    private boolean playerTabWasInit;

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        initDrawerLayout();

        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentResult(int resultCode, @Nullable Object data) {
        if (!playerTabWasInit) {
            PlayerFragment playerFragment = null;
            try {
                playerFragment = (PlayerFragment) pageAdapter.getFragment(viewPager, 0);
            } catch (Exception e) {
                Logger.error(e);
            }
            switch (resultCode) {
                case FragmentResult.TRACK_LIST_UPDATE:
                    try {
                        Track track = (Track) data;
                        if (playerFragment != null && track != null) {
                            playerFragment.hideProgress();
                            playerFragment.onTrackLoadingComplete();
                            playerFragment.onTrackRefresh(track);
                            playerTabWasInit = true;
                        }
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                    break;
                case FragmentResult.TRACK_LIST_LOADING_ERROR:
                    if (playerFragment != null) {
                        playerFragment.hideProgress();
                        playerFragment.onError(getString(R.string.fragment_player_error_no_tracks));
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (toggle.isDrawerIndicatorEnabled()) {
            showExitDialog();
        }
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
                    break;
                case R.id.menu_sync:
                    break;
                case R.id.menu_download_man:
                    break;
                case R.id.menu_settings:
                    break;
                case R.id.menu_about:
                    break;
                case R.id.menu_faq:
                    break;
                case R.id.menu_deauthorize:
                    break;

            }
            drawer.closeDrawer(GravityCompat.START);

            return true;
        });

        AppCompatTextView title = navigationView.getHeaderView(0).findViewById(R.id.title);
        title.setText(getString(R.string.title).concat(" v").concat(BuildConfig.VERSION_NAME));
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = Dialog.getAlertDialogBuilder(getString(R.string.dialog_title), getString(R.string.dialog_exit), this);
        builder.setNegativeButton(getString(R.string.dialog_no), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.dialog_yes), (dialog, which) -> {
            finishApp();
        });
        builder.show();
    }
}
