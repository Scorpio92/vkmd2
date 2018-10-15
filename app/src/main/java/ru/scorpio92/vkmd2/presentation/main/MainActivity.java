package ru.scorpio92.vkmd2.presentation.main;

import android.support.annotation.Nullable;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.base.BaseActivity;
import ru.scorpio92.vkmd2.presentation.base.IFragmentListener;
import ru.scorpio92.vkmd2.presentation.main.tracklist.TrackListFragment;
import ru.scorpio92.vkmd2.tools.ViewUtils;

public class MainActivity extends BaseActivity implements IFragmentListener {

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        ViewUtils.replaceFragment(getSupportFragmentManager(), R.id.container, new TrackListFragment());
    }

    @Override
    public void onFragmentResult(int resultCode, @Nullable Object data) {

    }
}
