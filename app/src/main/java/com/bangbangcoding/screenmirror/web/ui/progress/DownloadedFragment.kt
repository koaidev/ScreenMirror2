package com.bangbangcoding.screenmirror.web.ui.progress

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.VideoDownloadModel
import com.bangbangcoding.screenmirror.databinding.FragmentDownloadedBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.RenameDialog
import com.bangbangcoding.screenmirror.web.ui.player.VideoPlayerActivity
import com.bangbangcoding.screenmirror.web.ui.studio.model.DownloadItem
import com.bangbangcoding.screenmirror.web.ui.studio.multidownload.DownloadManager
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.ScreenUtil
import com.bangbangcoding.screenmirror.web.utils.StoreUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import javax.inject.Inject


class DownloadedFragment : BaseFragment() {

    companion object {
        fun newInstance() = DownloadedFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: DownloadedViewModel

    private var _binding: FragmentDownloadedBinding? = null
    private val binding get() = _binding!!

    private lateinit var downloadedAdapter: DownloadedAdapter
    private var isChecked = false
    private var oldPath: String? = null
    private var mNewName: String? = null
    private lateinit var mDownloadManager: DownloadManager

    private val perms = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncher1 =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[perms[0]] != null && result[perms[0]]!!) {
                setupRev()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.title_permission_request))
                    .setMessage(R.string.txt_please_allow_permission_download)
                    .setNegativeButton(R.string.txt_cancel) { _, _ ->
                        requireActivity().onBackPressed()
                    }.setPositiveButton(R.string.title_permission_request) { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
//                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .create().show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[DownloadedViewModel::class.java]

        _binding = FragmentDownloadedBinding.inflate(inflater, container, false)
        loadNativeAds()
        mDownloadManager = DownloadManager.getInstance()
        mDownloadManager.init(context)
        return binding.root
    }

//    private fun loadAds() {
//        val adRequest1: AdRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest1)
//    }

    private fun loadNativeAds() {
        val adLoader = AdLoader.Builder(requireContext(), Constants.ADS_NATIVE_ID)
            .forNativeAd { ad: NativeAd ->
                // Show the ad.
                val styles = NativeTemplateStyle.Builder().build()
                binding.myTemplate.setStyles(styles)
                binding.myTemplate.setNativeAd(ad)
                binding.myTemplate.visibility = View.VISIBLE
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    if (context != null) {
                        binding.myTemplate.visibility = View.GONE
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
    }

    private fun initPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.e("ttt", "initPermission: Granted")
                setupRev()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                Log.e("ttt", "initPermission: shouldShow")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
//            showInContextUI(...)
                requestPermissionLauncher1.launch(perms)
//                requireActivity().snackbar(R.string.txt_please_allow_permission_download)
            }
            else -> {
                Log.e("ttt", "initPermission: launch")
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher1.launch(perms)
            }
        }
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun setupRev() {
        val list = getListVideosCreated(requireContext())

        val downloadInfos = mDownloadManager.allDownloads
        val downloadItems: MutableList<DownloadItem> = ArrayList<DownloadItem>()
        for (downloadInfo in downloadInfos) {
            downloadItems.add(DownloadItem(downloadInfo))
        }

        if (list.isEmpty() && downloadItems.isEmpty()) {
            showNoVideo()
        }

        downloadedAdapter = DownloadedAdapter(
            list,
            downloadItems,
            {
                shareVideo(it)
            }, {
                playVideo(it)
            }, { model, v ->
                moreActionVideo(model, v)
            }, {
                isChecked = it
            })
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = downloadedAdapter
        }
        if (downloadedAdapter.isEmpty()) {
            showNoVideo()
        }
        showSpaceUsed()
    }

    private fun showSpaceUsed() {
        val uses = StoreUtils.getUsedExternalSize()
        val total = StoreUtils.getTotalExternalMemorySize()
        val msg = getString(R.string.text_used_space, uses, total)
        binding.tvSpace.text = msg
    }

    fun showCheckbox(isShow: Boolean) {
        downloadedAdapter.showAllCheckbox(isShow)
    }

    fun checkShow(isChecked: Boolean) {
        this.isChecked = isChecked
        downloadedAdapter.setAllChecked(isChecked)
    }

    fun getCheck(): Boolean {
        return isChecked
    }

    private fun getListVideosCreated(context: Context): List<VideoDownloadModel> {
        return StoreUtils.getListVideosCreatedExternal(context)
//        return StoreUtils.getVideos(context)
    }

    private fun playVideo(videoDownloadModel: VideoDownloadModel) {
//        startActivity(
//            IntentActivity.getIntent(requireContext(), VideoPlayerFragment::class.java,
//                args = Bundle().apply
//                {
//                    putParcelable(VideoPlayerFragment.KEY_VIDEO, videoDownloadModel)
//                })
//        )
        val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
        intent.putExtra(VideoPlayerActivity.KEY_VIDEO, videoDownloadModel)
        startActivity(intent)


//                        context.startActivity(new Intent(context, PlayActivity.class)
//                                .putExtra("videourl", file.getAbsolutePath())
//                                .putExtra(AppMeasurementSdk.ConditionalUserProperty.NAME, System.currentTimeMillis() + ""));
//        startActivity(
//            Intent(context, VideoPlayActivity::class.java)
//                .putExtra(VideoPlayActivity.KEY_VIDEO, videoDownloadModel)
//        )
    }

    private fun moreActionVideo(videoDownloadModel: VideoDownloadModel, view: View) {
        showMenu(videoDownloadModel, view)
    }

    private fun showMenu(videoModel: VideoDownloadModel, v: View) {
        val popupWindow = PopupWindow()
        val layout: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.popup_window_action, null)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            layout.findViewById<View>(R.id.tvRename).visibility = View.GONE
            layout.findViewById<View>(R.id.tvDelete).visibility = View.GONE
        }
        layout.findViewById<View>(R.id.tvRename).setOnClickListener {
            renameVideo(videoModel)
            popupWindow.dismiss()
        }
        layout.findViewById<View>(R.id.tvDelete).setOnClickListener {
            deleteVideo(videoModel)
            popupWindow.dismiss()
        }
        layout.findViewById<View>(R.id.tvEditWithVideoMaker).setOnClickListener {
//            gotoWebsite(videoModel)
            Utils.openApp(requireContext(), "com.kunkun.videoeditor.videomaker")
            popupWindow.dismiss()
        }
        layout.findViewById<View>(R.id.tvInfo).setOnClickListener {
            info(videoModel)
            popupWindow.dismiss()
        }
        popupWindow.contentView = layout
        popupWindow.width = Utils.dpToPixels(requireContext(), 210)
        popupWindow.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        popupWindow.isFocusable = true
        val currentLocation: IntArray = checkShowAtLocation(v)
        if (currentLocation[0] == 0 && currentLocation[1] == 0) {
            popupWindow.showAsDropDown(v)
        } else {
            popupWindow.showAtLocation(v, Gravity.TOP, currentLocation[0], currentLocation[1])
        }
    }

    private fun checkShowAtLocation(anchor: View): IntArray {
        val screenLocation = IntArray(2)
        anchor.getLocationOnScreen(screenLocation)
        val height: Int = ScreenUtil.getHeightScreen(requireContext())
        if (height > Utils.dpToPixels(requireContext(), 250) + screenLocation[1]) {
            return intArrayOf(0, 0)
        }
        screenLocation[1] -= Utils.dpToPixels(requireContext(), 192)
        return screenLocation
    }

    private fun renameVideo(videoModel: VideoDownloadModel) {
        val dialog = RenameDialog.newInstance(videoModel.title)
        dialog.listener = object : RenameDialog.OnRenameListener {
            override fun onRename(newName: String) {

                val urisToModify = listOf<Uri>(
                    ContentUris.withAppendedId(
                        MediaStore.Video.Media.getContentUri("external"),
                        StoreUtils.getVideoPathToMediaID(videoModel.path, requireContext())
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val editPendingIntent = MediaStore.createWriteRequest(
                        requireContext().contentResolver,
                        urisToModify
                    )
                    oldPath = videoModel.path
                    mNewName = newName
                    renameIntentSenderRequest.launch(
                        IntentSenderRequest.Builder(editPendingIntent).build()
                    )
                } else {
                    StoreUtils.renameFile(requireContext(), videoModel.path, newName) {
                        when (it) { //-1: exist; 0: OK; 1: Refresh
//                            -1 -> {
//                                requireActivity().snackbar(requireContext().getString(R.string.txt_error_video_exist))
//                            }
                            1 -> {
                                setupRev()
                            }
                        }
                    }
                }
            }
        }
        dialog.show(parentFragmentManager, "downloaded")
    }

    private val deleteIntentSenderRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.e("ttt", "deleteIntentSenderRequest: RESULT_OK")
                removeListFile()
            } else {

                Log.e("ttt", "deleteIntentSenderRequest: RESULT_Cancel")
            }
        }

    private val renameIntentSenderRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.e("ttt", "renameIntentSenderRequest: RESULT_OK")
                if (oldPath != null && mNewName != null) {
                    StoreUtils.renameFile(requireContext(), oldPath!!, mNewName!!) {
                        when (it) { //-1: exist; 0: OK; 1: Refresh
//                            -1 -> {
//                                requireActivity().snackbar(requireContext().getString(R.string.txt_error_video_exist))
//                            }
                            1 -> {
                                setupRev()
                            }
                        }
                    }
                }
            } else {
                Log.e("ttt", "renameIntentSenderRequest: RESULT_Cancel")
            }
        }

    private fun removeListFile() {
        listRemove.forEach {
            val file = File(it.path)
            if (file.delete()) {
                Log.e("ttt", "deleteMultipleVideo: delete ok")
                downloadedAdapter.remove(it)
                if (downloadedAdapter.isEmpty()) {
                    showNoVideo()
                }
            } else {
                StoreUtils.testDelete(requireContext(), it.path)
                Log.e("ttt", "deleteMultipleVideo: delete like shit")
            }
        }
        showCheckbox(false)
        checkShow(false)
        (parentFragment as DownloadFragment).showToolbarActionWithFile()
    }

    private fun requestWriteRequest(videos: List<VideoDownloadModel>) {
        val urisToModify = videos.map {
            ContentUris.withAppendedId(
                MediaStore.Video.Media.getContentUri("external"),
                StoreUtils.getVideoPathToMediaID(it.path, requireContext())
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val editPendingIntent = MediaStore.createWriteRequest(
                    requireContext().contentResolver,
                    urisToModify
                )
                deleteIntentSenderRequest.launch(
                    IntentSenderRequest.Builder(editPendingIntent).build()
                )
            } catch (e: Exception) {
                Log.e("requestWriteRequest", "" + e.toString())
            }

        } else {
            removeListFile()
        }
    }

    override fun onResume() {
        super.onResume()
        initPermission()
    }

    private fun gotoWebsite(videoDownloadModel: VideoDownloadModel) {
        Toast.makeText(requireContext(), "Go to website", Toast.LENGTH_SHORT).show()
    }

    private fun info(item: VideoDownloadModel) {
        val detail = """
            ${getString(R.string.txt_title)}: ${item.title}
            ${getString(R.string.resolution)}: ${item.width}x${item.height}
            ${getString(R.string.txt_size)}: ${item.getSizeVideo(requireContext())}
            ${getString(R.string.txt_path)}: ${item.path}
            """.trimIndent()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.txt_detail)
            .setMessage(detail)
            .setPositiveButton(R.string.txt_ok, null)
            .show()
    }

    private fun shareVideo(videoModel: VideoDownloadModel) {
        val file = File(videoModel.path)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".fileprovider",
            file
        )
        IntentBuilder(requireContext())
            .setStream(uri)
            .setType("video/mp4")
            .setChooserTitle("Share video...")
            .startChooser()
    }

    private fun deleteVideo(item: VideoDownloadModel) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.txt_confirm)
            .setMessage(R.string.txt_noti_delete_file)
            .setPositiveButton(R.string.txt_yes) { _, _ ->
                listRemove = listOf(item)
                requestWriteRequest(listRemove)
            }
            .setNegativeButton(R.string.txt_no, null)
            .show()
    }

    private var listRemove = listOf<VideoDownloadModel>()

    fun deleteMultipleVideo(successFunc: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.txt_confirm)
            .setMessage(R.string.txt_noti_delete_files)
            .setPositiveButton(R.string.txt_yes) { _, _ ->
                listRemove = downloadedAdapter.getListData().filter { it.checked }
                requestWriteRequest(listRemove)
            }
            .setNegativeButton(R.string.txt_no, null)
            .show()
    }

    private fun showNoVideo() {
        binding.tvNoDownload.visibility = View.VISIBLE
        binding.recycler.visibility = View.GONE
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateView(pos: Int?) {
        Log.e("ttt", "onUpdateView: AAAAA")
        setupRev()
    }
}
