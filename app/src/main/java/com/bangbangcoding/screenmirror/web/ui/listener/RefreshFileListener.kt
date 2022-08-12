package com.bangbangcoding.screenmirror.web.ui.listener

import android.net.Uri

interface RefreshFileListener {
    fun onValueFile(path: String?, uri: Uri?)
}
