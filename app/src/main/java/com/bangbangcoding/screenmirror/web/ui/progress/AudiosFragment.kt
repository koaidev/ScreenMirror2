package com.bangbangcoding.screenmirror.web.ui.progress

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.bangbangcoding.screenmirror.BuildConfig
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.AudioDownloadModel
import com.bangbangcoding.screenmirror.databinding.FragmentAudiosBinding
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.ui.dialog.RenameDialog
import com.bangbangcoding.screenmirror.web.ui.player.VideoPlayerActivity
import com.bangbangcoding.screenmirror.web.utils.ScreenUtil
import com.bangbangcoding.screenmirror.web.utils.StoreUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import java.io.File
import javax.inject.Inject

class AudiosFragment : BaseFragment() {

    companion object {
        fun newInstance() = AudiosFragment()
    }

    private lateinit var viewModel: AudiosViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _binding: FragmentAudiosBinding? = null
    private val binding get() = _binding!!

    private lateinit var downloadedAdapter: DownloadedAudioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, viewModelFactory)[AudiosViewModel::class.java]

        _binding = FragmentAudiosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUI() {
        super.setupUI()
        val list = getListVideosCreated(requireContext())
        downloadedAdapter = DownloadedAudioAdapter(
            list,
            {
                shareVideo(it)
            }, {
                playVideo(it)
            }, { model, v ->
                moreActionVideo(model, v)
            })
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = downloadedAdapter
        }
        if (list.isEmpty()) {
            showNoVideo()
        }
    }

    private fun getListVideosCreated(context: Context): List<AudioDownloadModel> {
        return StoreUtils.getListAudiosCreatedExternal(context)
    }

    private fun playVideo(videoDownloadModel: AudioDownloadModel) {
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
    }

    private fun moreActionVideo(videoDownloadModel: AudioDownloadModel, view: View) {
        showMenu(videoDownloadModel, view)
    }

    private fun showMenu(videoModel: AudioDownloadModel, v: View) {
        val popupWindow = PopupWindow()
        val layout: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.popup_window_action, null)
        layout.findViewById<View>(R.id.tvRename).setOnClickListener {
            renameVideo(videoModel)
            popupWindow.dismiss()
        }
        layout.findViewById<View>(R.id.tvDelete).setOnClickListener {
            deleteVideo(videoModel)
            popupWindow.dismiss()
        }
        layout.findViewById<View>(R.id.tvEditWithVideoMaker).setOnClickListener {
            gotoWebsite(videoModel)
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

    private fun renameVideo(videoModel: AudioDownloadModel) {
        val dialog = RenameDialog.newInstance(videoModel.title)
        dialog.listener = object : RenameDialog.OnRenameListener {
            override fun onRename(newName: String) {
                val oldPath: String = videoModel.path
                val file = File(oldPath)
                val currentFileName = oldPath.substring(oldPath.lastIndexOf("/"))
                val path = oldPath.replace(currentFileName, "/$newName.mp4")
                val fileTo = File(path)
                Log.d("ttt", "rename: OK$oldPath:::$path")
                if (fileTo.exists()) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.txt_error_video_exist),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
                if (file.exists()) {
                    if (file.renameTo(fileTo)) {
                        StoreUtils.refreshFile(requireContext(), fileTo)
                        videoModel.title = newName
                        videoModel.path = path
//                        videoModel.getUri(Uri.parse(path))
                        downloadedAdapter.notifyDataSetChanged()
                        Log.d("ttt", "deleteVideo: OK")
                    } else {
                        Log.d("ttt", "deleteVideo: Fail")
                    }
                }
            }
        }
        dialog.show(parentFragmentManager, "downloaded")
    }

    private fun gotoWebsite(audioDownloadModel: AudioDownloadModel) {
        Toast.makeText(requireContext(), "Go to website", Toast.LENGTH_SHORT).show()
    }

    private fun info(item: AudioDownloadModel) {
        val detail = """
            ${getString(R.string.txt_title)}: ${item.title}
            ${getString(R.string.resolution)}: ${item.width}x${item.height}
            ${getString(R.string.txt_size)}: ${
            Formatter.formatFileSize(
                requireContext(),
                File(item.path).length()
            )
        }
            ${getString(R.string.txt_path)}: ${item.path}
            
            """.trimIndent()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.txt_detail)
            .setMessage(detail)
            .setPositiveButton(R.string.txt_ok, null)
            .show()
    }

    private fun shareVideo(videoModel: AudioDownloadModel) {
        val file = File(videoModel.path)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".fileprovider",
            file
        )
        ShareCompat.IntentBuilder(requireContext())
            .setStream(uri)
            .setType("video/mp4")
            .setChooserTitle("Share video...")
            .startChooser()
    }

    private fun deleteVideo(item: AudioDownloadModel) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.txt_confirm)
            .setMessage(R.string.txt_noti_delete_file)
            .setPositiveButton(R.string.txt_yes) { dialog, which ->
                val file = File(item.path)
                if (file.delete()) {
                    StoreUtils.refreshFile(requireContext(), file)
                    downloadedAdapter.remove(item)
                    if (downloadedAdapter.isEmpty()) {
                        showNoVideo()
                    }
                }
            }
            .setNegativeButton(R.string.txt_no, null)
            .show()
    }

    private fun showNoVideo() {
        binding.tvNoDownload.visibility = View.VISIBLE
        binding.tvNoDownload.text = getString(R.string.txt_not_audio_yet)
        binding.recycler.visibility = View.GONE
    }
}
