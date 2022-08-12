package com.bangbangcoding.screenmirror.web.utils

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.annotation.DrawableRes

object AnimationUtils {

    @JvmStatic
    fun createRotationTransitionAnimation(
        imageView: ImageView,
        @DrawableRes drawableRes: Int
    ): Animation = object : Animation() {

        private var setFinalDrawable: Boolean = false

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) =
            if (interpolatedTime < 0.5f) {
                imageView.rotationY = 90f * interpolatedTime * 2f
            } else {
                if (!setFinalDrawable) {
                    setFinalDrawable = true
                    imageView.setImageResource(drawableRes)
                }
                imageView.rotationY = -90 + 90f * (interpolatedTime - 0.5f) * 2f
            }

    }.apply {
        duration = 300
        interpolator = AccelerateDecelerateInterpolator()
    }

}
