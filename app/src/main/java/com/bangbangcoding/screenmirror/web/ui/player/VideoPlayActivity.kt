//package com.highsecure.videodownloader.ui.player
//
//import android.app.PictureInPictureParams
//import android.content.Intent
//import android.content.res.Configuration
//import android.graphics.Rect
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.util.Rational
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.highsecure.videodownloader.R
//import com.highsecure.videodownloader.data.local.model.VideoDownloadModel
//import com.highsecure.videodownloader.databinding.ActivityVideoPlayBinding
//import com.highsecure.videodownloader.ui.CustomToast
//import com.universalvideoview.UniversalVideoView.VideoViewCallback
//import java.lang.Exception
//
//class VideoPlayActivity : AppCompatActivity() {
//    companion object {
//        const val KEY_VIDEO = "KEY_VIDEO_PLAY"
//        private const val ORIENTATION = "ORIENTATION"
//    }
//
////    var urls: String? = null
//    var pictureInPictureParamsBuilder: PictureInPictureParams.Builder? = null
//    private var _binding: ActivityVideoPlayBinding? = null
//    private var videoDownloadModel: VideoDownloadModel? = null
//
//    private val binding get() = _binding!!
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ActivityVideoPlayBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setupPlayer()
//        binding.imgPictureinpicture.setOnClickListener { startPictureInPictureFeature() }
//        binding.back.setOnClickListener { onBackPressed() }
//        binding.storyVideo.setOnClickListener {
//            binding.storyVideo.visibility = View.GONE
//            binding.videoView.start()
//        }
//    }
//
//    private fun setupPlayer() {
//        try {
//            videoDownloadModel = intent.getParcelableExtra(KEY_VIDEO)
//            binding.videoView.setMediaController(binding.mediaController)
//            binding.mediaController.setTitle(videoDownloadModel?.title)
//            binding.videoView.setVideoURI(Uri.parse(videoDownloadModel?.path))
//            binding.videoView.setVideoViewCallback(object : VideoViewCallback {
//                override fun onScaleChange(isFullscreen: Boolean) {}
//                override fun onPause(mediaPlayer: MediaPlayer) {
//                    binding.storyVideo.visibility = View.VISIBLE
//                }
//
//                override fun onStart(mediaPlayer: MediaPlayer) {
//                    binding.storyVideo.visibility = View.GONE
//                }
//                override fun onBufferingStart(mediaPlayer: MediaPlayer) { // steam start loading
//                }
//
//                override fun onBufferingEnd(mediaPlayer: MediaPlayer) { // steam end loading
//                }
//            })
//            binding.storyVideo.visibility = View.GONE
//            binding.videoView.start()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        setupPlayer()
//        super.onNewIntent(intent)
//    }
//
//    private fun startPictureInPictureFeature() {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
////            Rational aspectRatio = new Rational(2, 1);
//                pictureInPictureParamsBuilder = PictureInPictureParams.Builder()
////                val aspectRatio = Rational(16, 9)
//                val visibleRect = Rect()
//                binding.videoView.getGlobalVisibleRect(visibleRect)
//                val aspectRatio =
//                    Rational(binding.videoView.width, binding.videoView.height)
//                pictureInPictureParamsBuilder!!.setAspectRatio(aspectRatio).build()
//                setPictureInPictureParams(pictureInPictureParamsBuilder!!.build())
//                enterPictureInPictureMode(pictureInPictureParamsBuilder!!.build())
//            } else {
//                CustomToast.makeText(this, getString(R.string.pip_not_support),
//                    CustomToast.SHORT, CustomToast.WARNING).show()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            CustomToast.makeText(this, getString(R.string.pip_mode_error),
//                CustomToast.SHORT, CustomToast.WARNING).show()
//        }
//    }
//
//    override fun onUserLeaveHint() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (!isInPictureInPictureMode) {
//                val aspectRatio =
//                    Rational(binding.videoView.width, binding.videoView.height)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    try {
//                        pictureInPictureParamsBuilder!!.setAspectRatio(aspectRatio).build()
//                        enterPictureInPictureMode(pictureInPictureParamsBuilder!!.build())
//                    } catch (e: Exception) {
//                        //    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onPictureInPictureModeChanged(
//        isInPictureInPictureMode: Boolean,
//        newConfig: Configuration?
//    ) {
//        if (isInPictureInPictureMode) {
////            binding.videoView.hi()
//            binding.back.visibility = View.GONE
//            binding.imgPictureinpicture.visibility = View.GONE
//        } else {
//            binding.back.visibility = View.VISIBLE
//            binding.imgPictureinpicture.visibility = View.VISIBLE
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        binding.videoView.closePlayer()
//    }
//}
