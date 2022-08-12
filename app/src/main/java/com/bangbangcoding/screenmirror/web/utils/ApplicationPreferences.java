package com.bangbangcoding.screenmirror.web.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class ApplicationPreferences {

    private SharedPreferences mSharedPreferences = null;

    public ApplicationPreferences(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    public void addListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.mSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void removeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.mSharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
}
