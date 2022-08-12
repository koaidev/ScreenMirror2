package com.bangbangcoding.screenmirror.web.ui.player.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ViewSimpleClockBinding
import java.util.concurrent.TimeUnit

class CountDownClock : LinearLayout {
    private var countDownTimer: CountDownTimer? = null
    private var countdownListener: CountdownCallBack? = null
    private var countdownTickInterval = 1000

    private var almostFinishedCallbackTimeInSeconds: Int = 5

    private var resetSymbol: String = "0"

    private var binding: ViewSimpleClockBinding =
        ViewSimpleClockBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.view_simple_clock, this)

        attrs?.let {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.CountDownClock, defStyleAttr, 0)
            val resetSymbol = typedArray?.getString(R.styleable.CountDownClock_resetSymbol)
//            if (resetSymbol != null) {
//                setResetSymbol(resetSymbol)
//            }

            val digitTopDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitTopDrawable)
            setDigitTopDrawable(digitTopDrawable)
            val digitBottomDrawable = typedArray?.getDrawable(R.styleable.CountDownClock_digitBottomDrawable)
            setDigitBottomDrawable(digitBottomDrawable)
            val digitDividerColor = typedArray?.getColor(R.styleable.CountDownClock_digitDividerColor, 0)
            setDigitDividerColor(digitDividerColor ?: 0)
            val digitSplitterColor = typedArray?.getColor(R.styleable.CountDownClock_digitSplitterColor, 0)
            setDigitSplitterColor(digitSplitterColor ?: 0)

            val digitTextColor = typedArray?.getColor(R.styleable.CountDownClock_digitTextColor, 0)
            setDigitTextColor(digitTextColor ?: 0)

            val digitTextSize = typedArray?.getDimension(R.styleable.CountDownClock_digitTextSize, 0f)
            setDigitTextSize(digitTextSize ?: 0f)
            setSplitterDigitTextSize(digitTextSize ?: 0f)

            val digitPadding = typedArray?.getDimension(R.styleable.CountDownClock_digitPadding, 0f)
            setDigitPadding(digitPadding?.toInt() ?: 0)

            val splitterPadding = typedArray?.getDimension(R.styleable.CountDownClock_splitterPadding, 0f)
            setSplitterPadding(splitterPadding?.toInt() ?: 0)

            val halfDigitHeight = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_halfDigitHeight, 0)
            val digitWidth = typedArray?.getDimensionPixelSize(R.styleable.CountDownClock_digitWidth, 0)
            setHalfDigitHeightAndDigitWidth(halfDigitHeight ?: 0, digitWidth ?: 0)

            val animationDuration = typedArray?.getInt(R.styleable.CountDownClock_animationDuration, 0)
            setAnimationDuration(animationDuration ?: 600)

            val almostFinishedCallbackTimeInSeconds = typedArray?.getInt(R.styleable.CountDownClock_almostFinishedCallbackTimeInSeconds, 5)
            setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds ?: 5)

            val countdownTickInterval = typedArray?.getInt(R.styleable.CountDownClock_countdownTickInterval, 1000)
            this.countdownTickInterval = countdownTickInterval ?: 1000

            invalidate()
            typedArray?.recycle()
        }
    }

    ////////////////
    // Public methods
    ////////////////

    fun startCountDown(timeToNextEvent: Long) {
        countDownTimer?.cancel()
        var hasCalledAlmostFinished = false
        countDownTimer = object : CountDownTimer(timeToNextEvent, countdownTickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 <= almostFinishedCallbackTimeInSeconds && !hasCalledAlmostFinished) {
                    hasCalledAlmostFinished = true
                    countdownListener?.countdownAboutToFinish()
                }
                setCountDownTime(millisUntilFinished)
            }

            override fun onFinish() {
                hasCalledAlmostFinished = false
                countdownListener?.countdownFinished()
            }
        }
        countDownTimer?.start()
    }

    fun resetCountdownTimer() {
        countDownTimer?.cancel()
        binding.firstDigitMinute.setNewText(resetSymbol)
        binding.secondDigitMinute.setNewText(resetSymbol)
        binding.firstDigitSecond.setNewText(resetSymbol)
        binding.secondDigitSecond.setNewText(resetSymbol)
    }

    ////////////////
    // Private methods
    ////////////////

    private fun setCountDownTime(timeToStart: Long) {

        val days = TimeUnit.MILLISECONDS.toDays(timeToStart)
        val hours = TimeUnit.MILLISECONDS.toHours(timeToStart - TimeUnit.DAYS.toMillis(days))
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours)))
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToStart - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)))

        val minutesString = minutes.toString()
        val secondsString = seconds.toString()


        when {
            minutesString.length == 2 -> {
                binding.firstDigitMinute.animateTextChange((minutesString[0].toString()))
                binding.secondDigitMinute.animateTextChange((minutesString[1].toString()))
            }
            minutesString.length == 1 -> {
                binding.firstDigitMinute.animateTextChange(("0"))
                binding.secondDigitMinute.animateTextChange((minutesString[0].toString()))
            }
            else -> {
                binding.firstDigitMinute.animateTextChange(("5"))
                binding.secondDigitMinute.animateTextChange(("9"))
            }
        }
        when {
            secondsString.length == 2 -> {
                binding.firstDigitSecond.animateTextChange((secondsString[0].toString()))
                binding.secondDigitSecond.animateTextChange((secondsString[1].toString()))
            }
            secondsString.length == 1 -> {
                binding.firstDigitSecond.animateTextChange(("0"))
                binding.secondDigitSecond.animateTextChange((secondsString[0].toString()))
            }
            else -> {
                binding.firstDigitSecond.animateTextChange((secondsString[secondsString.length - 2].toString()))
                binding.secondDigitSecond.animateTextChange((secondsString[secondsString.length - 1].toString()))
            }
        }
    }

    private fun setResetSymbol(resetSymbol: String?) {
        resetSymbol?.let {
            if (it.isNotEmpty()) {
                this.resetSymbol = resetSymbol
            } else {
                this.resetSymbol = ""
            }
        } ?: kotlin.run {
            this.resetSymbol = ""
        }
    }

    private fun setDigitTopDrawable(digitTopDrawable: Drawable?) {
        if (digitTopDrawable != null) {
            binding.firstDigitMinute.setDigitTopDrawable(digitTopDrawable)
            binding.secondDigitMinute.setDigitTopDrawable(digitTopDrawable)
            binding.firstDigitSecond.setDigitTopDrawable(digitTopDrawable)
            binding.secondDigitSecond.setDigitTopDrawable(digitTopDrawable)
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitBottomDrawable(digitBottomDrawable: Drawable?) {
        if (digitBottomDrawable != null) {
            binding.firstDigitMinute.setDigitTopDrawable(digitBottomDrawable)
            binding.secondDigitMinute.setDigitTopDrawable(digitBottomDrawable)
            binding.firstDigitSecond.setDigitTopDrawable(digitBottomDrawable)
            binding.secondDigitSecond.setDigitTopDrawable(digitBottomDrawable)
        } else {
            setTransparentBackgroundColor()
        }
    }

    private fun setDigitDividerColor(digitDividerColor: Int) {
        var dividerColor = digitDividerColor
        if (dividerColor == 0) {
            dividerColor = ContextCompat.getColor(context, R.color.transparent)
        }
        binding.firstDigitMinute.setDigitBg(dividerColor)
        binding.secondDigitMinute.setDigitBg(dividerColor)
        binding.firstDigitSecond.setDigitBg(dividerColor)
        binding.secondDigitSecond.setDigitBg(dividerColor)
    }

    private fun setDigitSplitterColor(digitsSplitterColor: Int) {
        if (digitsSplitterColor != 0) {
            //  digitsSplitter.setTextColor(digitsSplitterColor)
        } else {
            // digitsSplitter.setTextColor(ContextCompat.getColor(context, R.color.transparent))
        }
    }

    private fun setSplitterDigitTextSize(digitsTextSize: Float) {
        //digitsSplitter.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitsTextSize)
    }

    private fun setDigitPadding(digitPadding: Int) {
        binding.firstDigitMinute.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitMinute.setPadding(digitPadding, digitPadding, digitPadding , digitPadding)
        binding.firstDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
        binding.secondDigitSecond.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
    }

    private fun setSplitterPadding(splitterPadding: Int) {
        //digitsSplitter.setPadding(splitterPadding, 0, splitterPadding, 0)
    }

    private fun setDigitTextColor(digitsTextColor: Int) {
        var textColor = digitsTextColor
        if (textColor == 0) {
            textColor = ContextCompat.getColor(context, R.color.transparent)
        }

        binding.firstDigitMinute.setDigitTextColor(textColor)
        binding.secondDigitMinute.setDigitTextColor(textColor)
        binding.firstDigitSecond.setDigitTextColor(textColor)
        binding.secondDigitSecond.setDigitTextColor(textColor)
    }

    private fun setDigitTextSize(digitsTextSize: Float) {
        binding.firstDigitMinute.setTextSize(digitsTextSize)
        binding.secondDigitMinute.setTextSize(digitsTextSize)
        binding.firstDigitSecond.setTextSize(digitsTextSize)
        binding.secondDigitSecond.setTextSize(digitsTextSize)
    }

    private fun setHalfDigitHeightAndDigitWidth(halfDigitHeight: Int, digitWidth: Int) {
        binding.firstDigitMinute.setHeightAndWidthToView(halfDigitHeight, digitWidth)
        binding.secondDigitMinute.setHeightAndWidthToView(halfDigitHeight, digitWidth)
        binding.firstDigitSecond.setHeightAndWidthToView(halfDigitHeight, digitWidth)
        binding.secondDigitSecond.setHeightAndWidthToView(halfDigitHeight, digitWidth)

        // Dividers
        binding.firstDigitMinute.setLayoutParamsWidth(digitWidth)
        binding.secondDigitMinute.setLayoutParamsWidth(digitWidth)
        binding.firstDigitSecond.setLayoutParamsWidth(digitWidth)
        binding.secondDigitSecond.setLayoutParamsWidth(digitWidth)
    }

    private fun setAnimationDuration(animationDuration: Int) {
        binding.firstDigitMinute.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitMinute.setAnimationDuration(animationDuration.toLong())
        binding.firstDigitSecond.setAnimationDuration(animationDuration.toLong())
        binding.secondDigitSecond.setAnimationDuration(animationDuration.toLong())
    }

    private fun setAlmostFinishedCallbackTimeInSeconds(almostFinishedCallbackTimeInSeconds: Int) {
        this.almostFinishedCallbackTimeInSeconds = almostFinishedCallbackTimeInSeconds
    }

    private fun setTransparentBackgroundColor() {
        val transparent = ContextCompat.getColor(context, R.color.transparent)
        binding.firstDigitMinute.setBackground(transparent)
        binding.secondDigitMinute.setBackground(transparent)
        binding.firstDigitSecond.setBackground(transparent)
        binding.secondDigitSecond.setBackground(transparent)
    }

    ////////////////
    // Listeners
    ////////////////

    public fun setCountdownListener(countdownListener: CountdownCallBack) {
        this.countdownListener = countdownListener
    }

    public interface CountdownCallBack {
        fun countdownAboutToFinish()
        fun countdownFinished()
    }
}
