package com.bangbangcoding.screenmirror.web.utils

import android.os.Build
import android.view.View
import android.view.Window

object SystemUiUtils {
    fun showStatusNavigationBar(window: Window, isShow: Boolean) {
        if (isShow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.decorView.windowInsetsController!!.show(
                        android.view.WindowInsets.Type.statusBars()
                )
                window.decorView.windowInsetsController!!.show(
                        android.view.WindowInsets.Type.navigationBars()
                )

                window.decorView.windowInsetsController!!.show(
                        android.view.WindowInsets.Type.displayCutout()
                )
            } else {
                showSystemUI(window)
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.decorView.windowInsetsController!!.hide(
                        android.view.WindowInsets.Type.statusBars()
                )
                window.decorView.windowInsetsController!!.hide(
                        android.view.WindowInsets.Type.navigationBars()
                )

                window.decorView.windowInsetsController!!.hide(
                        android.view.WindowInsets.Type.displayCutout()
                )
            } else {
                hideSystemUI(window)
            }
        }
    }

    fun hideSystemUI(window: Window) { // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView: View = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI(window: Window) {
        val decorView: View = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}