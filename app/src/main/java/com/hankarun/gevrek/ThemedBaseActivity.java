package com.hankarun.gevrek;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public abstract class ThemedBaseActivity extends AppCompatActivity {
    private int currentTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentTheme = getCurrentTheme();

        setTheme(currentTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        if(currentTheme != getCurrentTheme())
        {
            currentTheme = getCurrentTheme();
            setTheme(currentTheme);
            finish();
            startActivity(getIntent());
        }

        super.onResume();
    }

    int getCurrentTheme()
    {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_dark_theme","0");

        if (theme.equals("0")) return R.style.AppTheme_Dark;

        return R.style.AppTheme;
    }
}
