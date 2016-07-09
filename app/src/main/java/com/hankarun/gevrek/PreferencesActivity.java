package com.hankarun.gevrek;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class PreferencesActivity extends ThemedBaseActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction().replace(R.id.content_frame1, new SettingsFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}