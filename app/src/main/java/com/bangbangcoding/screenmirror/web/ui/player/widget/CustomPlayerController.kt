package com.bangbangcoding.screenmirror.web.ui.player.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Handler
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.utils.PreferenceUtils
import com.bangbangcoding.screenmirror.web.utils.SystemUiUtils
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.android.synthetic.main.exo_controller.view.*

class CustomPlayerController : RelativeLayout, View.OnClickListener {
    private val DEFAULT_TIME_HIDE_CONTROL = 2000
    var activity: Activity? = null
    lateinit var listener: OnClickPlayerControlListener
    private var isFullscreen = true
    private var isFirst = true
    private var maxGestureLength: Int = 0
    private lateinit var gestureDetector: GestureDetector
    private val MAX_GESTURE_LENGTH = 0.75f
    private val maxBrightness = 255f
    lateinit var audioManager: AudioManager
    var currentProgressVolume: Float? = null
    var currentProgressBrightness: Float? = null
    private var isScrollScreen: Boolean = false
    var controllerHideOnTouch = true
    var isPlaying = false
    var handlerHideView: Handler? = null
    var runableHideView: Runnable? = null
    var isLockControl = false
    lateinit var countDownClock: CountDownClock
    private fun bindView() {
        toolbar.setNavigationOnClickListener {
            listener.onControlClickBack()
        }

        btnPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            changeStatusPlaying(isPlaying)
            listener.onControllerChangeStatusPlaying(isPlaying)
        }
        countDownClock = findViewById(R.id.countDownVideo)

        btnRotate.setOnClickListener(this)
        btnAspectRatio.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnPrevious.setOnClickListener(this)
        btnNowPlaying.setOnClickListener(this)
        btnLock.setOnClickListener(this)
        btnSpeed.setOnClickListener(this)
        btnFavorite.setOnClickListener(this)
        btnMore.setOnClickListener(this)
        btnPip.setOnClickListener(this)
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init()
    }

    fun setOnClickConTrolListener(mActivity: Activity, mListener: OnClickPlayerControlListener) {
        listener = mListener
        activity = mActivity
    }

    private fun init() {
        inflate(context, R.layout.exo_controller, this)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initView() {
        bindView()

        autoHideControllerDelay()
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.icon_small_pause)
        } else {
            btnPlayPause.setImageResource(R.drawable.icon_small_play)
        }
        initAspectRatio()

        audioManager = ContextCompat.getSystemService(context, AudioManager::class.java)!!

        seekBarVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    listener.onSeekChangeProgress(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    listener.onDoubleTabController()
                    return super.onDoubleTap(e)
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    val currentOrientation = resources.configuration.orientation
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (e1.x > rootController.width / 8 && e1.x < rootController.width - rootController.width / 8 && e1.y > rootController.height / 8 && e1.y < rootController.height - rootController.height / 8) {
                            isScrollScreen = true
                            showOrHideControl(false)
                            val widthSeparate = rootController.width / 2
                            if (e1.x > widthSeparate / 5 && e1.x < (widthSeparate - widthSeparate / 3)) {
                                progressBarBrightness.alpha = 1f
                                progressBarBrightness.visibility = View.VISIBLE
                                changeBrightness(distanceY)
                            } else if (e1.x < rootController.width - widthSeparate / 5 && e1.x > widthSeparate + widthSeparate / 3) {
                                progressBarVolume.alpha = 1f
                                progressBarVolume.visibility = View.VISIBLE
                                changeVolume(distanceY)
                            }
                        }
                    } else {
                        if (e1.y > rootController.height / 8 && e1.y < rootController.height - rootController.height / 8) {
                            isScrollScreen = true
                            showOrHideControl(false)
                            val widthSeparate = rootController.width / 2
                            if (e1.x > widthSeparate / 5 && e1.x < (widthSeparate - widthSeparate / 3)) {
                                progressBarBrightness.alpha = 1f
                                progressBarBrightness.visibility = View.VISIBLE
                                changeBrightness(distanceY)
                            } else if (e1.x < rootController.width - widthSeparate / 5 && e1.x > widthSeparate + widthSeparate / 3) {
                                progressBarVolume.alpha = 1f
                                progressBarVolume.visibility = View.VISIBLE
                                changeVolume(distanceY)
                            }
                        }
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }

            })

        rootController.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (isFirst || left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                val width = right - left
                val height = bottom - top
                val min = width.coerceAtMost(height)
                maxGestureLength = ((min * MAX_GESTURE_LENGTH).toInt())
                progressBarBrightness.max = maxGestureLength.toFloat()
                progressBarVolume.max = maxGestureLength.toFloat()
                progressBarBrightness.postDelayed(Runnable {
                    setDefaultVolume()
                    setDefaultBrightness()
                }, 1000)
                isFirst = false
            }
        }

        rootController.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    showController(true)
                }
                MotionEvent.ACTION_UP -> {
                    if (isPlaying) {
                        if (isScrollScreen) {
                            if (progressBarBrightness.visibility == View.VISIBLE) progressBarBrightness.showOrHide(
                                false,
                                250
                            )
                            if (progressBarVolume.visibility == View.VISIBLE) progressBarVolume.showOrHide(
                                false,
                                250
                            )
                            isScrollScreen = false
                            showController(false)
                        } else {
                            autoHideControllerDelay()
                        }
                    } else {
                        if (isScrollScreen) {
                            if (progressBarBrightness.visibility == View.VISIBLE) progressBarBrightness.showOrHide(
                                false,
                                250
                            )
                            if (progressBarVolume.visibility == View.VISIBLE) progressBarVolume.showOrHide(
                                false,
                                250
                            )
                            isScrollScreen = false
                        }
                        showController(true)
                    }
                }
            }
            true
        }
    }

    fun updateStatusPlaying(boolean: Boolean?) {
        isPlaying = boolean ?: false
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause_video)
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_video)
        }
    }

    fun setTitleVideo(title: String?) {
        tvTitleVideo.text = title
        tvTitleVideo.isSelected = true
    }

    private fun autoHideControllerDelay() {
        if (handlerHideView != null) {
            runableHideView?.let { handlerHideView?.removeCallbacks(it) }
        }

        handlerHideView = Handler()
        runableHideView = Runnable {
            activity?.window?.let { SystemUiUtils.showStatusNavigationBar(it, false) }
            showController(false)
            runableHideView = null
            handlerHideView = null
        }
        handlerHideView?.postDelayed(runableHideView!!, DEFAULT_TIME_HIDE_CONTROL.toLong())
    }

    private fun lockControl(isLock: Boolean) {
        if (isLock) {
            btnLock.setImageResource(R.drawable.ic_unlock_video)
            showOrHideControl(false)
            viewLockBg.visibility = View.VISIBLE
        } else {
            btnLock.setImageResource(R.drawable.ic_lock_video)
            showOrHideControl(true)
            viewLockBg.visibility = View.INVISIBLE
        }
    }

    fun showOrHideControl(isShow: Boolean) {
        if (isShow) {
            activity?.window?.let { SystemUiUtils.showStatusNavigationBar(it, true) }
            toolbar.visibility = View.VISIBLE
            controlPlayer.visibility = View.VISIBLE
            durationContainer.visibility = View.VISIBLE
//            btnAspectRatio.visibility = View.VISIBLE
//            btnSpeed.visibility = View.VISIBLE
//            btnPip.visibility = View.VISIBLE
            btnRotate.visibility = View.VISIBLE
            viewLockBg.visibility = View.GONE
        } else {
            activity?.window?.let { SystemUiUtils.showStatusNavigationBar(it, false) }
            toolbar.visibility = View.INVISIBLE
            controlPlayer.visibility = View.INVISIBLE
            durationContainer.visibility = View.INVISIBLE
            btnAspectRatio.visibility = View.INVISIBLE
            btnSpeed.visibility = View.INVISIBLE
            btnPip.visibility = View.INVISIBLE
            btnRotate.visibility = View.INVISIBLE
            viewLockBg.visibility = View.VISIBLE
        }
    }

    fun showController(isShow: Boolean) {
        if (handlerHideView != null) {
            runableHideView?.let { handlerHideView?.removeCallbacks(it) }
        }
        if (!isLockControl) {
            if (isShow) {
                animate().setInterpolator(FastOutSlowInInterpolator()).alpha(1f).setDuration(350)
                    .setStartDelay(0).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            showOrHideControl(true)
                        }
                    }).start()
            } else {
                animate().setInterpolator(FastOutSlowInInterpolator()).alpha(0f).setDuration(350)
                    .setStartDelay(0).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            showOrHideControl(false)
                        }
                    }).start()
            }
        } else {
            if (isShow) {
                animate().setInterpolator(FastOutSlowInInterpolator()).alpha(1f).setDuration(350)
                    .setStartDelay(0).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            activity?.window?.let {
                                SystemUiUtils.showStatusNavigationBar(
                                    it,
                                    true
                                )
                            }
                        }
                    }).start()
            } else {
                animate().setInterpolator(FastOutSlowInInterpolator()).alpha(0f).setDuration(350)
                    .setStartDelay(0).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            activity?.window?.let {
                                SystemUiUtils.showStatusNavigationBar(
                                    it,
                                    false
                                )
                            }
                        }
                    }).start()
            }
        }
    }

    private fun setDefaultBrightness() {
        if (currentProgressBrightness == null) {
            val curBrightnessValue =
                Settings.System.getInt(activity?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            val percent = curBrightnessValue.toFloat() / maxBrightness
            currentProgressBrightness = (percent * maxGestureLength)
        }
        progressBarBrightness.progress = currentProgressBrightness!!.toFloat()
    }

    private fun setDefaultVolume() {
        if (currentProgressVolume == null) {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val percent = currentVolume.toFloat() / maxVolume.toFloat()
            currentProgressVolume = (percent * maxGestureLength)
        }
        progressBarVolume.progress = currentProgressVolume!!.toFloat()
    }

    private fun changeBrightness(distanceY: Float) {
        progressBarBrightness.incrementProgressBy(distanceY.toInt())
        val currentProgress = progressBarBrightness.progress / progressBarBrightness.max
        val layoutParams = activity?.window?.attributes
        currentProgressBrightness = progressBarBrightness.progress
        layoutParams?.screenBrightness = currentProgress
        activity?.window?.attributes = layoutParams
    }

    private fun changeVolume(distanceY: Float) {
        progressBarVolume.incrementProgressBy(distanceY.toInt())
        currentProgressVolume = progressBarVolume.progress
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentProgressPercent = progressBarVolume.progress / progressBarVolume.max
        val currentVolume = maxVolume * currentProgressPercent
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume.toInt(), 0)
    }

    fun changeStatusPlaying(boolean: Boolean?) {
        isPlaying = boolean ?: false
        controllerHideOnTouch = if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause_video)
            autoHideControllerDelay()
            true
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_video)
            showController(true)
            false
        }
    }

    private fun rotateScreen() {
        val currentOrientation = resources.configuration.orientation
        activity?.requestedOrientation =
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                isFullscreen = false
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                isFullscreen = true
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
    }

    fun setDefaultAspectRatio() {
        PreferenceUtils().putRatioVideo(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        initAspectRatio()
        listener.onControlChangeRatioAspect(AspectRatioFrameLayout.RESIZE_MODE_FIT)
    }

    fun initAspectRatio() {
        when (PreferenceUtils().getRatioVideo()) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_16_9)
            }
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_fit)
            }
            else -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_full)
            }
        }
    }

    private fun changeAspectRatio() {
        when (PreferenceUtils().getRatioVideo()) {
            AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_fit)
                listener.onControlChangeRatioAspect(AspectRatioFrameLayout.RESIZE_MODE_FILL)
                PreferenceUtils().putRatioVideo(AspectRatioFrameLayout.RESIZE_MODE_FILL)
            }
            AspectRatioFrameLayout.RESIZE_MODE_FILL -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_full)
                listener.onControlChangeRatioAspect(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                PreferenceUtils().putRatioVideo(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
            }
            else -> {
                btnAspectRatio.setImageResource(R.drawable.ic_ratio_16_9)
                listener.onControlChangeRatioAspect(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                PreferenceUtils().putRatioVideo(AspectRatioFrameLayout.RESIZE_MODE_FIT)
            }
        }
    }

    private fun showDialogChangeSpeed() {
//        val dialogSpeed = Dialog(context)
//        dialogSpeed.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialogSpeed.setContentView(R.layout.dialog_change_speed_video)
//        val window = dialogSpeed.window
//        val wlp = window!!.attributes
//        wlp.gravity = Gravity.CENTER
//        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
//        dialogSpeed.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        window.attributes = wlp
//
//        dialogSpeed.window!!.setLayout(
//            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
//        )
//        val groupSpeed = dialogSpeed.findViewById<RadioGroup>(R.id.rdSpeedGroup)
//        groupSpeed.check(getIDPlaybackSpeedFromValue())
//        groupSpeed.setOnCheckedChangeListener { _, checkedId ->
//            var speedValue = AppConstants.PLAYBACK_SPEED.SPEED_DEFAULT
//            when (checkedId) {
//                R.id.rdSpeed05x -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_05
//                }
//                R.id.rdSpeed075x -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_075
//                }
//                R.id.rdSpeedNormal -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_DEFAULT
//                }
//                R.id.rdSpeed125x -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_125
//                }
//                R.id.rdSpeed15x -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_15
//                }
//                R.id.rdSpeed2x -> {
//                    speedValue = AppConstants.PLAYBACK_SPEED.SPEED_2
//                }
//            }
//            PreferenceUtils().putPlaybackSpeed(speedValue)
//            listener.onControlClickChangeSpeed()
//            dialogSpeed.dismiss()
//        }
//        dialogSpeed.show()
    }

    private fun getIDPlaybackSpeedFromValue() {
//        var id = R.id.rdSpeedNormal
//        when (PreferenceUtils().getPlaybackSpeed()) {
//            AppConstants.PLAYBACK_SPEED.SPEED_05 -> {
//                id = R.id.rdSpeed05x
//            }
//            AppConstants.PLAYBACK_SPEED.SPEED_075 -> {
//                id = R.id.rdSpeed075x
//            }
//            AppConstants.PLAYBACK_SPEED.SPEED_DEFAULT -> {
//                id = R.id.rdSpeedNormal
//            }
//            AppConstants.PLAYBACK_SPEED.SPEED_125 -> {
//                id = R.id.rdSpeed125x
//            }
//            AppConstants.PLAYBACK_SPEED.SPEED_15 -> {
//                id = R.id.rdSpeed15x
//            }
//            AppConstants.PLAYBACK_SPEED.SPEED_2 -> {
//                id = R.id.rdSpeed2x
//            }
//        }
//        return id
    }

    fun updateFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_video_on)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_video)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAspectRatio -> {
                changeAspectRatio()
            }
            R.id.btnRotate -> {
                rotateScreen()
            }
            R.id.btnNext -> {
                listener.onControlClickNext()
            }
            R.id.btnPrevious -> {
                listener.onControlClickPrevious()
            }
            R.id.btnNowPlaying -> {
                listener.onControlClickNowPlaying()
            }
            R.id.btnLock -> {
                isLockControl = !isLockControl
                lockControl(isLockControl)
            }
            R.id.btnSpeed -> {
                showDialogChangeSpeed()
            }
            R.id.btnFavorite -> {
                listener.onClickFavorite()
            }
            R.id.btnMore -> {
                listener.onControlClickMore()
            }
            R.id.btnPip -> {
                Log.e("Click", "Pip")
                listener.onControlClickPipMode()
            }
        }
        if (isPlaying) {
            autoHideControllerDelay()
        }
    }

    interface OnClickPlayerControlListener {
        fun onClickFavorite()
        fun onControlClickBack()
        fun onControlClickNowPlaying()
        fun onControlClickMore()
        fun onControlClickPrevious()
        fun onControllerChangeStatusPlaying(isPlay: Boolean)
        fun onControlClickNext()
        fun onSeekChangeProgress(value: Long)
        fun onControlChangeRatioAspect(ratio: Int)
        fun onControlClickChangeSpeed()
        fun onControlClickPipMode()
        fun onControlClickRotate()
        fun onControlClickLockScreen()
        fun onClickController()
        fun onDoubleTabController()
    }
}



