package com.bangbangcoding.screenmirror.web.utils.ext

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * Removes a view from its parent if it has one.
 */
fun View?.removeFromParent() = this?.let {
    val parent = it.parent
    (parent as? ViewGroup)?.removeView(it)
}

/**
 * Performs an action when the view is laid out.
 *
 * @param runnable the runnable to run when the view is laid out.
 */
inline fun View?.doOnLayout(crossinline runnable: () -> Unit) = this?.let {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            runnable()
        }
    })
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}