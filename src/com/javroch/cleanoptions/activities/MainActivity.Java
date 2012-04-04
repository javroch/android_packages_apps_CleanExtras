package com.javroch.cleanoptions.activities;

import com.javroch.cleanoptions.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.clean_options);
    }
}
