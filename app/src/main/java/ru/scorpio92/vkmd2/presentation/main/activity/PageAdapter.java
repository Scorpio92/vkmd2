package ru.scorpio92.vkmd2.presentation.main.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.scorpio92.vkmd2.presentation.main.fragment.player.PlayerFragment;
import ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.TrackListFragment;

public class PageAdapter extends FragmentPagerAdapter {

    PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new PlayerFragment();
            case 1:
                return new TrackListFragment();
            default:
                throw new IllegalArgumentException("unknown fragment");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Сейчас играет";
            case 1:
                return "Мои аудиозаписи";
            default:
                throw new IllegalArgumentException("unknown fragment");
        }
    }
}
