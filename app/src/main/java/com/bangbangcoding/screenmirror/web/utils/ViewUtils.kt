package com.bangbangcoding.screenmirror.web.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.google.android.material.snackbar.Snackbar

object ViewUtils {

    fun showSnackBar(rootView: View, s: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(rootView, s, duration).show()
    }

    fun rotateView(view: View) {
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 1000
        rotate.interpolator = LinearInterpolator()
        view.startAnimation(rotate)
    }
}
