package com.bangbangcoding.screenmirror.web.ui.feature

import android.graphics.Bitmap
import com.bangbangcoding.screenmirror.web.ui.widget.LightningView

class TabViewState(private val lightningView: LightningView) {

    val title: String = lightningView.title
    val favicon: Bitmap = lightningView.favicon
    val isForegroundTab = lightningView.isForegroundTab
    var isSelect = lightningView.isShown || lightningView.focus
    val screenShot = lightningView.screenShot

    override fun equals(other: Any?): Boolean =
        other is TabViewState && other.lightningView == lightningView

    override fun hashCode(): Int {
        var result = lightningView.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + favicon.hashCode()
        result = 31 * result + isForegroundTab.hashCode()
        return result
    }

}
