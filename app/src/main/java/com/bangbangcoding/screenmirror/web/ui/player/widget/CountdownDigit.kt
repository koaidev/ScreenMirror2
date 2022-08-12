package com.bangbangcoding.screenmirror.web.ui.player.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.bangbangcoding.screenmirror.R
import kotlinx.android.synthetic.main.view_countdown_clock_digit.view.*

class CountDownDigit : FrameLayout {
    private var animationDuration = 600L

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.view_countdown_clock_digit, this)

        frontUpperText.measure(0, 0)
        frontLowerText.measure(0, 0)
        backUpperText.measure(0, 0)
        backLowerText.measure(0, 0)
    }

    fun setNewText(newText: String) {
        frontUpper.clearAnimation()
        frontLower.clearAnimation()

        frontUpperText.text = newText
        frontLowerText.text = newText
        backUpperText.text = newText
        backLowerText.text = newText
    }

    fun animateTextChange(newText: String) {
        if (backUpperText.text == newText) {
            return
        }

        frontUpper.clearAnimation()
        frontLower.clearAnimation()

        backUpperText.text = newText
        frontUpper.pivotY = frontUpper.bottom.toFloat()
        frontLower.pivotY = frontUpper.top.toFloat()
        frontUpper.pivotX =
            (frontUpper.right - ((frontUpper.right - frontUpper.left) / 2)).toFloat()
        frontLower.pivotX =
            (frontUpper.right - ((frontUpper.right - frontUpper.left) / 2)).toFloat()

        frontUpper.animate().setDuration(getHalfOfAnimationDuration()).rotationX(-90f)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                frontUpperText.text = backUpperText.text
                frontUpper.rotationX = 0f
                frontLower.rotationX = 90f
                frontLowerText.text = backUpperText.text
                frontLower.animate().setDuration(getHalfOfAnimationDuration()).rotationX(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        backLowerText.text = frontLowerText.text
                    }.start()
            }.start()
    }

    fun setHeightAndWidthToView(halfDigitHeight: Int, digitWidth: Int) {
        setHeightAndWidthToView(frontUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(backUpper, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(frontLower, halfDigitHeight, digitWidth)
        setHeightAndWidthToView(backLower, halfDigitHeight, digitWidth)
    }

    private fun setHeightAndWidthToView(view: View, halfDigitHeight: Int, digitWidth: Int) {
        val firstDigitMinuteFrontUpperLayoutParams = view.layoutParams
        firstDigitMinuteFrontUpperLayoutParams.height = halfDigitHeight
        firstDigitMinuteFrontUpperLayoutParams.width = digitWidth
    }

    fun setTextSize(digitsTextSize: Float) {
        frontUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        backUpperText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
        frontLowerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    fun setDigitTextColor(textColor: Int) {
        frontUpperText.setTextColor(textColor)
        backUpperText.setTextColor(textColor)
        frontLowerText.setTextColor(textColor)
        backLowerText.setTextColor(textColor)
    }

    fun setDigitBg(dividerColor: Int) {
        digitDivider.setBackgroundColor(dividerColor)
    }

    fun setDigitTopDrawable(digitTopDrawable: Drawable) {
        frontUpper.background = digitTopDrawable
        backUpper.background = digitTopDrawable
    }

    fun setBackground(transparent: Int) {
        frontLower.setBackgroundColor(transparent)
        backLower.setBackgroundColor(transparent)
    }

    fun setLayoutParamsWidth(digitWidth: Int) {
        digitDivider.layoutParams.width = digitWidth
    }

    fun setAnimationDuration(duration: Long) {
        this.animationDuration = duration
    }

    private fun getHalfOfAnimationDuration(): Long {
        return animationDuration / 2
    }
}
