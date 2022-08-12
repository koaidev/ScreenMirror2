package com.bangbangcoding.screenmirror.web.ui.progress

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.VideoDownloadModel
import com.bangbangcoding.screenmirror.databinding.ItemDownloadPageBinding
import com.bangbangcoding.screenmirror.databinding.ItemDownloadProcessBinding
import com.bangbangcoding.screenmirror.web.ui.studio.model.DownloadItem
import com.bangbangcoding.screenmirror.web.utils.DateTimeUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import kotlinx.android.synthetic.main.item_download_page.view.*

class DownloadedAdapter(
    private var videoDownloadModels: List<VideoDownloadModel>,
    private var downloadItems: List<DownloadItem>,
    private val shareFunc: (VideoDownloadModel) -> Unit,
    private val playFunc: (VideoDownloadModel) -> Unit,
    private val moreFunc: (VideoDownloadModel, View) -> Unit,
    private val checkedFunc: (Boolean) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val binding = ItemDownloadPageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )

            return DownloadedViewHolder(binding, shareFunc, playFunc, moreFunc, checkedFunc)
        } else {
            val binding = ItemDownloadProcessBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )

            return DownloadProcessViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sizeProcess = downloadItems.size
        if(getItemViewType(position) == 0) {
            (holder as DownloadProcessViewHolder).bind(videoDownloadModel = downloadItems[position])
        } else {
            (holder as DownloadedViewHolder).bind(videoDownloadModel = videoDownloadModels[position - sizeProcess], (position - sizeProcess))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (videoDownloadModels.isEmpty())  {
             return 0
        }
        if (downloadItems.isEmpty())  {
            return 1
        }
        return if (position < downloadItems.size) 0 else 1
    }


    override fun getItemCount(): Int {
        Log.e("ttt", "getItemCount: " + videoDownloadModels.size + "/" + downloadItems.size)
        return videoDownloadModels.size + downloadItems.size
    }

    fun remove(videoDetail: VideoDownloadModel) {
        (videoDownloadModels as ArrayList).remove(videoDetail)
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return videoDownloadModels.isEmpty() && downloadItems.isEmpty()
    }

    fun getListData(): List<VideoDownloadModel> {
        return videoDownloadModels
    }

    fun showAllCheckbox(isShow : Boolean) {
        videoDownloadModels.forEach {
            it.isShow = isShow
        }
        notifyDataSetChanged()
    }

    fun setAllChecked(checked: Boolean) {
        videoDownloadModels.forEach {
            it.checked = checked
        }
        notifyDataSetChanged()
    }


    inner class DownloadProcessViewHolder(
        private val binding: ItemDownloadProcessBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(videoDownloadModel: DownloadItem) {
            binding.downloadView.setDownloadItem(videoDownloadModel)
        }
    }

    inner class DownloadedViewHolder(
        private val binding: ItemDownloadPageBinding,
        private val shareFunc: (VideoDownloadModel) -> Unit,
        private val playFunc: (VideoDownloadModel) -> Unit,
        private val moreFunc: (VideoDownloadModel, View) -> Unit,
        private val checkedFunc: (Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

        }

        fun bind(videoDownloadModel: VideoDownloadModel, pos: Int) {
            val position = videoDownloadModels.indexOf(videoDownloadModel)
            itemView.setOnClickListener {
                Log.e("ttt", "click play: $position ::: $pos")
                playFunc.invoke(videoDownloadModels[position])
            }

            binding.ivMenu.setOnClickListener { view ->
                Log.e("ttt", "click moreFunc: $position")
                moreFunc.invoke(videoDownloadModels[position], view)
            }

            binding.ivShare.setOnClickListener {
                Log.e("ttt", "click shareFunc: $position")
                shareFunc.invoke(videoDownloadModels[position])
            }
            binding.checkbox.setOnCheckedChangeListener { _, p1 ->
                videoDownloadModels[position].checked = p1
                val check = videoDownloadModels.findLast { it.checked }
                checkedFunc.invoke(check != null)
            }

            binding.tvName.text = videoDownloadModel.title
            binding.tvSize.text = videoDownloadModel.getSizeVideo(binding.root.context)
            binding.tvDate.text = DateTimeUtils.formatDateTime(videoDownloadModel.createDate)
            Log.e("ttt", "Date: " + videoDownloadModel.createDate)
            binding.tvDuration.text = DateTimeUtils.formatTime(videoDownloadModel.duration)

            if(videoDownloadModel.isShow) {
                binding.checkbox.visibility = View.VISIBLE
                binding.checkbox.isChecked = videoDownloadModel.checked
            } else {
                binding.checkbox.visibility = View.GONE
            }

            Glide.with(binding.imgIcon.context)
                .asBitmap()
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .error(R.drawable.ic_thumb_song2)
                        .placeholder(R.drawable.ic_thumb_song9)
                )
                .load(videoDownloadModel.path)
                .into(object : ImageViewTarget<Bitmap?>(binding.imgIcon) {
                    override fun setResource(resource: Bitmap?) {
                        val roundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(binding.imgIcon.context.resources, resource)
                        roundedBitmapDrawable.cornerRadius =
                            Utils.dpToPixels(binding.imgIcon.context, 4).toFloat()
                        binding.imgIcon.setImageDrawable(roundedBitmapDrawable)
                    }
                })
        }
    }
}