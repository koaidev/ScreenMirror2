//package com.highsecure.videodownloader.ui.studio
//
//import android.content.pm.ActivityInfo
//import android.graphics.Color
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.FileProvider
//import com.google.android.exoplayer2.ExoPlaybackException
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import java.io.File
//
//class VideoPreviewActivity : AppCompatActivity() {
//    private var mPlayer: SimpleExoPlayer? = null
//    private var mPath: String? = null
//    private var currentPosition: Long = 0
//    private var isPlay = true
//    private var orientation: Int = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_preview_video)
//        ButterKnife.bind(this)
//        init()
//    }
//
//    private fun init() {
//        val intent = intent
//        if (intent != null) {
//            mPath = intent.getStringExtra(Const.PREVIEW_URL)
//            if (!TextUtils.isEmpty(mPath)) {
//                try {
//                    initPlayer()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        ivRotation.setOnClickListener {
//            if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                requestedOrientation = orientation
//            }else if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                requestedOrientation = orientation
//            }
//        }
//    }
//
//    private fun initPlayer() {
//        try {
//            mPlayer?.playWhenReady = false
//            mPlayer = SimpleExoPlayer.Builder(this).build()
//            val uri: Uri = if (mPath?.contains(BuildConfig.APPLICATION_ID)!!) {
//                Uri.fromFile(File(mPath))
//            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    FileProvider.getUriForFile(
//                            this,
//                            BuildConfig.APPLICATION_ID.toString() + ".fileprovider",
//                            File(mPath)
//                    )
//                } else {
//                    Uri.fromFile(File(mPath))
//                }
//            }
//
//
//            val userAgent =
//                    Util.getUserAgent(this, "videodownloader")
//            val mediaSource = ProgressiveMediaSource.Factory(
//                    DefaultDataSourceFactory(this, userAgent),
//                    DefaultExtractorsFactory()
//            ).createMediaSource(uri)
//
//            mPlayer?.prepare(mediaSource)
////            player?.playWhenReady = true
//            val listener = object : Player.EventListener {
//
//                override fun onPlayerError(error: ExoPlaybackException) {
//                    super.onPlayerError(error)
//                }
//
//                override fun onSeekProcessed() {}
//
//                override fun onLoadingChanged(isLoading: Boolean) {}
//
//                override fun onPositionDiscontinuity(reason: Int) {}
//
//                override fun onRepeatModeChanged(repeatMode: Int) {}
//
//                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
//
//                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                    when (playbackState) {
//                        ExoPlayer.STATE_ENDED -> {
//                            mPlayer?.seekTo(0)
//                            mPlayer?.playWhenReady = false
//                        }
//                        ExoPlayer.STATE_READY -> {
//                        }
//                        ExoPlayer.STATE_BUFFERING -> {
//                        }
//                        ExoPlayer.STATE_IDLE -> {
//                        }
//                    }
//                }
//            }
//            mPlayer?.addListener(listener)
//            videoPreview?.setShutterBackgroundColor(Color.TRANSPARENT)
//            videoPreview?.requestFocus()
//            videoPreview?.player = mPlayer
//
//        } catch (e: Exception) {
//            Log.d("TAG", "EX: " + e.message)
//        }
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        requestedOrientation = orientation
//        if (mPlayer != null) {
//            mPlayer!!.release()
//        }
//        initPlayer()
//        mPlayer?.seekTo(currentPosition)
//        mPlayer?.playWhenReady = isPlay
//        mPlayer?.playbackState
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        if (mPlayer != null) {
//            isPlay = mPlayer!!.playWhenReady
//            mPlayer!!.playWhenReady = false
//            currentPosition = mPlayer!!.currentPosition
//        }
//    }
//
//    public override fun onDestroy() {
//        if (mPlayer != null) {
//            mPlayer?.release()
//        }
//        super.onDestroy()
//    }
//
//    override fun onBackPressed() {
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        finish()
//    }
//}