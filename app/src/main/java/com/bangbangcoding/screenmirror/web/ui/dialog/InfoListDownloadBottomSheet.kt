package com.bangbangcoding.screenmirror.web.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bangbangcoding.screenmirror.databinding.BottomsheetQualityListBinding
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.utils.DateTimeUtils


class InfoListDownloadBottomSheet : BottomSheetDialogFragment() {
    companion object {
        private const val KEY_THUMBNAIL = "KEY_LINK_SHEET"
        private const val KEY_DURATION = "KEY_DURATION_SHEET"
        private const val KEY_TITLE = "KEY_TITLE_SHEET"
        private const val KEY_DATA = "KEY_DATA_SHEET"

        fun newInstance(
            title: String, duration: String, thumbnail: String?,
            videos: ArrayList<VideoInfo>
        ): InfoListDownloadBottomSheet {
            val args = Bundle()
            args.putString(KEY_DURATION, duration)
            args.putString(KEY_TITLE, title)
            args.putString(KEY_THUMBNAIL, thumbnail)
            args.putParcelableArrayList(KEY_DATA, videos)
            val fragment = InfoListDownloadBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var listDownloadAdapter: ListDownloadAdapter

    private var _binding: BottomsheetQualityListBinding? = null
    private val binding get() = _binding!!
    private var videoSelected: VideoInfo? = null
    private var mTitle: String? = null
    var mListener: DownloadBottomSheetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetQualityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mTitle = it.getString(KEY_TITLE)
            binding.tvTitle.text = mTitle
            var durationStr = it.getString(KEY_DURATION)
            if (durationStr != null && !durationStr.contains(":")) {
                var time = 0L
                try {
                    time = durationStr.toLong() * 1000L
                } catch (e: Exception) {
                    Log.e("ttt", "Exception duration: ${e.message}")
                }
                durationStr = DateTimeUtils.formatTime(time)
            }

            binding.tvDuration.text = durationStr
            var list = it.getParcelableArrayList<VideoInfo>(KEY_DATA)
            if (list != null && list.isNotEmpty()) {
                videoSelected = list[0]
//                if(list[0].fileSize == 0L) {
//                    val urls = list.take(8).map { video -> video.url!! }
//                    TaskRunner().executeAsync(
//                        LongRunningTask(urls),
//                        object : TaskRunner.Callback<List<Long>> {
//                            override fun onComplete(result: List<Long>) {
//                                list?.forEachIndexed { index, video ->
//                                    list!![index].fileSize = result[index]
//                                }
//                                listDownloadAdapter.setData(list!!.take(8))
//                            }
//                        })
//                }
            } else {
                list = arrayListOf<VideoInfo>()
            }

            listDownloadAdapter = ListDownloadAdapter(
                list.take(8),
                object : ListDownloadAdapter.QualityVideoListener {
                    override fun onItemClicked(pageInfo: VideoInfo) {
                        videoSelected = pageInfo
                    }
                }
            )
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = listDownloadAdapter
            }

            it.getString(KEY_THUMBNAIL)?.let { link ->
                if (link.isNotEmpty()) {
                    Glide.with(requireContext()).load(link).into(binding.ivThumbnail)
                }
            }
        }

        binding.imageView5.setOnClickListener {
            dismiss()
        }

        binding.tvRename.setOnClickListener {
            val dialog = RenameDialog.newInstance(mTitle ?: "Downloader")
            dialog.listener = object : RenameDialog.OnRenameListener {
                override fun onRename(newName: String) {
                    videoSelected?.title = newName
                    binding.tvTitle.text = newName
                }
            }
            dialog.show(parentFragmentManager, "BS")
        }


        binding.clDownload.setOnClickListener {
            mListener?.onDownload(videoSelected!!)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        videoSelected?.isSelect = false
        super.onDismiss(dialog)
    }

    interface DownloadBottomSheetListener {
        fun onDownload(video: VideoInfo)
    }
}