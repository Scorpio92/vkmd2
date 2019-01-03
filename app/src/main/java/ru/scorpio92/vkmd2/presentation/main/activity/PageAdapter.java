package ru.scorpio92.vkmd2.presentation.main.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import ru.scorpio92.vkmd2.presentation.main.fragment.search.SearchFragment;
import ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.TrackListFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;

    PageAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new TrackListFragment();
            case 1:
                return new SearchFragment();
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
                return "Мои аудиозаписи";
            case 1:
                return "Онлайн поиск";
            default:
                throw new IllegalArgumentException("unknown fragment");
        }
    }

    private String getFragmentName(int viewId, int position) {
        return "android:switcher:" + viewId + ":" + position;
    }

    Fragment getFragment(ViewPager viewPager, int position) {
        String name = getFragmentName(viewPager.getId(), position);
        return fm.findFragmentByTag(name);
    }
}
