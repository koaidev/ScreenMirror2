package com.bangbangcoding.screenmirror.web.storage

import android.content.Context
import javax.inject.Inject

class DownloaderPreferences @Inject constructor(context: Context): BasePreferences(context, PREF_NAME) {

    companion object {
        private const val PREF_NAME = "PREF_DOWNLOADER"
        // user chose Don't ask again for Camera before.
        // Whether you can show permission rationale UI. If its value is false, it means
        const val KEY_LAST_FILE = "KEY_LAST_FILE"
    }
}