package ru.scorpio92.vkmd2.presentation.old.view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.old.view.fragment.SettingsFragment;


public class SettingsActivity extends AppCompatActivity {

    public static final String ACTIVE_KEY = "activeMode";
    public static final String CIRCULAR_PLAYING_KEY = "circularPlaying";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
    }
}
