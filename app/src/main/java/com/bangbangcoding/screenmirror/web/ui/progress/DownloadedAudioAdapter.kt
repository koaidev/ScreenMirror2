package com.bangbangcoding.screenmirror.web.ui.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.web.data.local.model.AudioDownloadModel
import com.bangbangcoding.screenmirror.databinding.ItemDownloadAudioBinding
import com.bangbangcoding.screenmirror.web.utils.DateTimeUtils

class DownloadedAudioAdapter(
    private var audioDownloadModels: List<AudioDownloadModel>,
    private val shareFunc: (AudioDownloadModel) -> Unit,
    private val playFunc: (AudioDownloadModel) -> Unit,
    private val moreFunc: (AudioDownloadModel, View) -> Unit,
) : RecyclerView.Adapter<DownloadedAudioAdapter.DownloadedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedViewHolder {
        val binding = ItemDownloadAudioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DownloadedViewHolder(binding, shareFunc, playFunc, moreFunc)
    }

    override fun onBindViewHolder(holder: DownloadedViewHolder, position: Int) {
        holder.bind(AudioDownloadModel = audioDownloadModels[position])
    }

    override fun getItemCount(): Int {
        return audioDownloadModels.size
    }

    fun remove(videoDetail: AudioDownloadModel) {
        (audioDownloadModels as ArrayList).remove(videoDetail)
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return audioDownloadModels.isEmpty()
    }

    inner class DownloadedViewHolder(
        private val binding: ItemDownloadAudioBinding,
        private val shareFunc: (AudioDownloadModel) -> Unit,
        private val playFunc: (AudioDownloadModel) -> Unit,
        private val moreFunc: (AudioDownloadModel, View) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                playFunc.invoke(audioDownloadModels[bindingAdapterPosition])
            }

            binding.ivMenu.setOnClickListener { view ->
                moreFunc.invoke(audioDownloadModels[bindingAdapterPosition], view)
            }

            binding.ivShare.setOnClickListener {
                shareFunc.invoke(audioDownloadModels[bindingAdapterPosition])
            }
        }

        fun bind(AudioDownloadModel: AudioDownloadModel) {
            binding.tvName.text = AudioDownloadModel.title
            binding.tvSize.text = AudioDownloadModel.size
            binding.tvDate.text = DateTimeUtils.formatDateTime(AudioDownloadModel.createDate)
            binding.tvDuration.text = DateTimeUtils.formatTime(AudioDownloadModel.duration)
//            Glide.with(binding.imgIcon.context).load(AudioDownloadModel.thumbnail)
//                .into(binding.imgIcon)
//            binding.root.setOnClickListener {
//                topPagesListener.onItemClicked(pageInfo)
//            }
        }
    }
}