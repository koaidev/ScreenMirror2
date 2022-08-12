package com.bangbangcoding.screenmirror.web.ui.player

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.VideoDownloadModel
import com.bangbangcoding.screenmirror.databinding.FragmentVideoPlayerBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseActivity
import com.bangbangcoding.screenmirror.web.ui.player.widget.CustomPlayerController
import com.bangbangcoding.screenmirror.web.utils.AppUtil
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.SystemUiUtils
import kotlinx.android.synthetic.main.custom_exo_player_control_view.view.*
import kotlinx.android.synthetic.main.exo_controller.view.*
import java.io.File
import javax.inject.Inject

class VideoPlayerActivity : BaseActivity(), StyledPlayerControlView.VisibilityListener,
    CustomPlayerController.OnClickPlayerControlListener {

    companion object {
        const val KEY_VIDEO = "KEY_VIDEO"
        fun newInstance() = VideoPlayerActivity()
        private const val ORIENTATION = "ORIENTATION"
    }

    var pictureInPictureParamsBuilder: PictureInPictureParams.Builder? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: VideoPlayerViewModel

    private var _binding: FragmentVideoPlayerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var videoDownloadModel: VideoDownloadModel? = null

    private lateinit var playerView: StyledPlayerView
    private var mPlayer: ExoPlayer? = null
    private var uri: Uri? = null
    private var isPortrait = true
    private var times = 0L
    private var savedInstanceState: Bundle? = null


    private var mInterstitialAd: InterstitialAd? = null
    private var isShowAd = false


    var videoPosition: Int = 0
    var mInterval: Long = 1000

    private val mProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            mPlayer?.let { player ->
                try {
                    if (isPlaying()) {
                        binding.controller.let {
                            it.seekBarVideo?.max = player.duration.toInt()
                            it.tvDuration?.text =
                                AppUtil.convertDuration(player.duration.toInt())
                            it.seekBarVideo?.progress =
                                player.currentPosition.toInt()
                            it.tvCurrentDuration?.text =
                                AppUtil.convertDuration(player.currentPosition.toInt())
                            it.seekBarVideo?.postDelayed(this, mInterval)
                        }
                    }
                } catch (ex: java.lang.Exception) {
                }
            }
        }
    }

    fun isPlaying(): Boolean {
        return if (mPlayer != null) {
            mPlayer?.playWhenReady == true
        } else {
            false
        }
    }

    //    private AdView adView;
//    private val adContainerView: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
        super.onCreate(savedInstanceState)
        if (this.savedInstanceState != null) {
            times = savedInstanceState?.getLong("abc") ?: 0L
            isPortrait = savedInstanceState?.getBoolean(ORIENTATION) ?: true
        }
        Log.e("ttt", "onCreate: : $times : $isPortrait")
        hideSystemBars()

        viewModel =
            ViewModelProvider(this, viewModelFactory)[VideoPlayerViewModel::class.java]

        _binding = FragmentVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.playerView
        videoDownloadModel = intent.getParcelableExtra(KEY_VIDEO)
        setupUI()
    }

    private fun hideSystemBars() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            window.decorView.systemUiVisibility =
//                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        } else {
//            val windowInsetsController =
//                ViewCompat.getWindowInsetsController(window.decorView) ?: return
//            // Configure the behavior of the hidden system bars
//            windowInsetsController.systemBarsBehavior =
//                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
//            // Hide both the status bar and the navigation bar
//            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//        }
    }

    private fun showSystemBars() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            );
        } else {
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(window.decorView) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
            // Hide both the status bar and the navigation bar
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun setListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // TODO: The system bars are visible. Make any desired
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
            } else {
                // TODO: The system bars are NOT visible. Make any desired
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
            }
        }
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        viewModel =
//            ViewModelProvider(this, viewModelFactory)[VideoPlayerViewModel::class.java]
//
//        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
//        playerView = binding.playerView
////        videoDownloadModel = arguments?.getParcelable(KEY_VIDEO)
//        videoDownloadModel = intent.getParcelableExtra(KEY_VIDEO)
//        return binding.root
//    }

    fun setupUI() {
        binding.controller.setOnClickConTrolListener(this, this)
        binding.tvTitle.text = videoDownloadModel!!.title
        binding.ivCancel.setOnClickListener {
            finish()
        }
        loadInterstitialAd(this)
    }

    private fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, Constants.ADS_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("TAG", "onAdLoaded")
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.d("TAG", "The ad was dismissed.")
                                if (isShowAd) {
                                    backToHome()
                                    //                                            finish();
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null
                                Log.d("TAG", "The ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                binding.clMain.rootView.visibility = View.GONE
                                // Called when fullscreen content is shown.
                                Log.d("TAG", "The ad was shown.")
                                isShowAd = true
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i("TAG", loadAdError.message)
                    mInterstitialAd = null
                    if (isShowAd) {
                        backToHome()
                        //                            finish();
                    }
                }
            })
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this)
        } else {
            backToHome()
            //            super.onBackPressed();
        }
    }

    override fun onBackPressed() {
        showInterstitial()
    }

    private fun backToHome() {
//        saveDraft()
//        mPanel = null
//        mThumbnailManager.release()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onBackPressed()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) SystemUiUtils.hideSystemUI(window)
    }

    private fun initializePlayer() {
        mPlayer = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        if (uri == null) {
            uri = Uri.fromFile(File(videoDownloadModel!!.path))
            binding.controller.setTitleVideo(videoDownloadModel?.title)
        }

        playerView.player = mPlayer
        mPlayer!!.setMediaItem(MediaItem.fromUri(uri!!))
        mPlayer!!.prepare()
        if (mPlayer != null) {
            if (times > 0) {
                mPlayer!!.seekTo(times)
            }
            mPlayer!!.play()
            mProgressRunnable.run()

            binding.controller.updateStatusPlaying(isPlaying())
        }

// Set the media item to be played.
// Prepare the player.

//        binding.playerControlView.setOnFullScreenModeChangedListener {
//            if (it) {
//                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            } else {
//                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            }
//        }
//        binding.playerControlView.showVrButton

        playerView.setControllerVisibilityListener(this)

        playerView.showController()

        playerView.setShowRewindButton(true)
        playerView.setShowFastForwardButton(true)
        playerView.setRepeatToggleModes(REPEAT_TOGGLE_MODE_ALL)


        playerView.setControllerOnFullScreenModeChangedListener {
//            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER

//            if (it) {
//                snackbar("Show full $it")
//            }

            requestedOrientation = if (isPortrait) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                // else change to Portrait
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            // opposite the value of isPortrait
            isPortrait = !isPortrait
        }

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            Log.e("ttt", "onStart: ")
            if (savedInstanceState == null) {
                initializePlayer()
            } else {
                mPlayer?.play()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || mPlayer == null) {
            Log.e("ttt", "onResume: ")
            initializePlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 23) {
            Log.e("ttt", "onPause: ")
            playerView.onPause()
        } else {
            mPlayer!!.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT <= 23) {
            Log.e("ttt", "onStop: ")
            playerView.onPause()
        } else {
            mPlayer!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ttt", "onDestroy: ")
        releasePlayer()
    }

    private fun releasePlayer() {
        if (mPlayer != null) {
            mPlayer!!.release()
            binding.controller.seekBarVideo?.progress = 0
            binding.controller.seekBarVideo?.max = 0
            binding.controller.seekBarVideo?.removeCallbacks(mProgressRunnable)
            mPlayer = null
        }
    }

    override fun onVisibilityChange(visibility: Int) {
        binding.ivCancel.visibility = visibility
        binding.tvTitle.visibility = visibility
        if (visibility == View.VISIBLE) {
            showSystemBars()
        } else {
            hideSystemBars()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e("ttt", "onConfigurationChanged: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedInstanceState = outState
        outState.putLong("abc", mPlayer?.currentPosition ?: 0L)
        outState.putBoolean(ORIENTATION, isPortrait)
        Log.e("ttt", "onSaveInstanceState: ${mPlayer?.currentPosition} ||| $isPortrait")
    }

    override fun onClickFavorite() {
        //TODO("Not yet implemented")
    }

    override fun onControlClickBack() {
        onBackPressed()
    }

    override fun onControlClickNowPlaying() {
        //TODO("Not yet implemented")
    }

    override fun onControlClickMore() {
        //TODO("Not yet implemented")
    }

    override fun onControlClickPrevious() {
        mPlayer?.let {
            val rewindTime = AppUtil.getDurationRewind(it.duration)
            if(it.currentPosition > rewindTime) {
                it.seekTo(it.currentPosition - rewindTime)
            }
        }
    }

    override fun onControllerChangeStatusPlaying(isPlay: Boolean) {
        if (mPlayer != null) {
            if (isPlaying()) {
                mPlayer?.playWhenReady = false
                playerView.keepScreenOn = false
            } else {
                mPlayer?.playWhenReady = true
                playerView.keepScreenOn = true
            }
            binding.controller.seekBarVideo?.max = mPlayer?.duration?.toInt() ?: 0
            binding.controller.seekBarVideo?.postDelayed(mProgressRunnable, mInterval)
            mProgressRunnable.run()
            binding.controller.updateStatusPlaying(isPlaying())
        }
    }

    override fun onControlClickNext() {
        mPlayer?.let {
            val rewindTime = AppUtil.getDurationRewind(it.duration)
            if(it.duration - it.currentPosition > rewindTime) {
                it.seekTo(it.currentPosition + rewindTime)
            }
        }
    }

    override fun onSeekChangeProgress(value: Long) {
        mPlayer?.seekTo(value)
    }

    override fun onControlChangeRatioAspect(ratio: Int) {
        changeAspectRatio(ratio)
    }

    private fun changeAspectRatio(ratio: Int) {
        binding.playerView.resizeMode = ratio
    }


    override fun onControlClickChangeSpeed() {
//        TODO("Not yet implemented")
    }

    override fun onControlClickPipMode() {
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            Handler().postDelayed({
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                    showDialogNeedMask(videoDownloadModel)
//                } else {
//                    videoPlayerSevice?.createWindowManagerPlayer()
//                    finish()
//                }
//            }, 1000)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                showDialogNeedMask(videoDownloadModel)
            } else {
//                videoPlayerSevice?.createWindowManagerPlayer()
                finish()
            }
        }
    }

    override fun onControlClickRotate() {
//        TODO("Not yet implemented")
    }

    override fun onControlClickLockScreen() {
//        TODO("Not yet implemented")
    }

    override fun onClickController() {
        if (binding.controller.visibility == View.VISIBLE) {
            binding.controller.showController(false)
        }
    }

    override fun onDoubleTabController() {
//        TODO("Not yet implemented")
    }
}
