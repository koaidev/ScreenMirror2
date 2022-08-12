package com.bangbangcoding.screenmirror.web.ui.dialog

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.databinding.ItemQualityVideoBinding
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.utils.Utils

class ListDownloadAdapter(
    private var pageInfos: List<VideoInfo>,
    private val qualityVideoListener: QualityVideoListener
) : RecyclerView.Adapter<ListDownloadAdapter.QualityVideoViewHolder>() {
    private var currentChecked = 0

    init {
        if (pageInfos.isNotEmpty()) {
            pageInfos[0].isSelect = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityVideoViewHolder {
        val binding = ItemQualityVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return QualityVideoViewHolder(binding, qualityVideoListener)
    }

    override fun getItemCount() = pageInfos.size

    override fun onBindViewHolder(holder: QualityVideoViewHolder, position: Int) =
        holder.bind(pageInfos[position])

    inner class QualityVideoViewHolder(
        private val binding: ItemQualityVideoBinding,
        private val qualityVideoListener: QualityVideoListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(videoInfo: VideoInfo) {
            if (pageInfos!!.size == 1) {
                var videoSize = Utils.getStringSizeLengthFile(videoInfo.fileSize)
                binding.rbSelect.isChecked = true
                binding.tvQuality.text = videoInfo.getQualityVideo()
                binding.tvSize.text = videoSize ?: "Undefined"
            } else {
                binding.rbSelect.isChecked = videoInfo.isSelect

                Log.e("ttt", "file ${videoInfo.format} : ${videoInfo.protocol}")
                videoInfo.url?.let {
                    binding.tvQuality.text = videoInfo.getQualityVideo()
                    if (it.contains(".m3u8") && videoInfo.protocol != null && videoInfo.protocol!!.contains("http")) {
                        binding.tvQuality.text = if (videoInfo.format!!.length > 20) {
                            videoInfo.formatID
                        } else {
                            videoInfo.format
                        }
                    }
                    var videoSize = Utils.getStringSizeLengthFile(videoInfo.fileSize)
                    videoSize = videoSize.replace(",", ".")
                    binding.tvSize.text = if (videoSize != "") videoSize + "" else ""
                    binding.rbSelect.setOnCheckedChangeListener { _, isChecked ->
                        videoInfo.isSelect = isChecked
                        if (isChecked) {
                            val oldPos = currentChecked
                            currentChecked = pageInfos.indexOf(videoInfo)
                            if (oldPos != 0 || oldPos != currentChecked) {
                                pageInfos[oldPos].isSelect = false
                                notifyItemChanged(oldPos)
                            }
                            qualityVideoListener.onItemClicked(videoInfo)
                        }
                    }
                }
            }

            binding.root.setOnClickListener {
                qualityVideoListener.onItemClicked(videoInfo)
                binding.rbSelect.isChecked = true
            }
        }
    }

    fun setData(pageInfos: List<VideoInfo>) {
        this.pageInfos = pageInfos
        notifyDataSetChanged()
    }

    interface QualityVideoListener {
        fun onItemClicked(pageInfo: VideoInfo)

    }
}
