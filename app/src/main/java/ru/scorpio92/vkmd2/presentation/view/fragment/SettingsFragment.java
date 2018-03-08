package ru.scorpio92.vkmd2.presentation.view.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import ru.scorpio92.vkmd2.R;


public class SettingsFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
