package com.bangbangcoding.screenmirror.web.ui.preference

import android.content.SharedPreferences
import com.bangbangcoding.screenmirror.web.di.module.DevPrefs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeveloperPreferences @Inject constructor(
    @DevPrefs preferences: SharedPreferences
) {

    var useLeakCanary by preferences.booleanPreference(LEAK_CANARY, false)

    var checkedForTor by preferences.booleanPreference(INITIAL_CHECK_FOR_TOR, false)

    var checkedForI2P by preferences.booleanPreference(INITIAL_CHECK_FOR_I2P, false)
}

private const val LEAK_CANARY = "leakCanary"
private const val INITIAL_CHECK_FOR_TOR = "checkForTor"
private const val INITIAL_CHECK_FOR_I2P = "checkForI2P"
