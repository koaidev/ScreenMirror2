package com.bangbangcoding.screenmirror.web.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ViewDownloadBinding
import com.bangbangcoding.screenmirror.web.ui.studio.model.DownloadItem
import com.bangbangcoding.screenmirror.web.ui.studio.model.DownloadProcessEvent
import com.bangbangcoding.screenmirror.web.utils.PrefUtils
import com.bangbangcoding.screenmirror.web.utils.ShareUtils
import java.io.File
import java.util.Locale
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DownloadView @JvmOverloads constructor(context: Context,
                               attrs: AttributeSet? = null,
                               defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var mDownloadItem: DownloadItem? = null

    private var binding: ViewDownloadBinding =
        ViewDownloadBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.let {
            it.btnDownload.setOnClickListener {
                reDownload()
            }
        }
    }

    private fun reDownload() {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onDetachedFromWindow() {
        EventBus.getDefault().unregister(this)
        super.onDetachedFromWindow()
    }

    @Subscribe
    fun onDownloadProcessEvent(downloadProcessEvent: DownloadProcessEvent) {
        if (mDownloadItem != null && mDownloadItem!!.url == downloadProcessEvent.mDownloadItem.url) {
            mDownloadItem = downloadProcessEvent.mDownloadItem
            updateView(mDownloadItem)
        }
    }

    private fun updateView(downloadItem: DownloadItem?) {
        binding?.let { binding ->
            binding.tvName.text = downloadItem!!.name
            Glide.with(context)
                .asBitmap()
                .load(downloadItem.url)
                .placeholder(R.drawable.ic_thumb_d)
                .error(R.drawable.ic_thumb_d)
                .into(binding.ivThumb)
            binding.progressBar.progress = downloadItem.progress
            binding.tvDownloadPerSize.text = String.format(
                Locale.ENGLISH,
                "%d%%, %s",
                downloadItem.progress,
                downloadItem.downloadPerSize
            )
            if (downloadItem.status == 3) {
                binding.txtPausePercent.visibility = GONE
                binding.btnDownload.setImageResource(R.drawable.ic_pause)
            } else if (downloadItem.status == 4 || downloadItem.status==5 ) {
                binding.txtPausePercent.visibility = VISIBLE
                binding.btnDownload.setImageResource(R.drawable.ic_download)
            } else if (downloadItem.status == 6) {
                EventBus.getDefault().postSticky(6)
            }else if (downloadItem.status == 0 ) {
                binding.root.visibility = GONE
            }

        }
    }

    fun setDownloadItem(downloadItem: DownloadItem?) {
        if(downloadItem!!.name == null || downloadItem!!.name.isEmpty()){
            downloadItem!!.name = PrefUtils.getString(context, downloadItem.url);
        }
        mDownloadItem = downloadItem
        updateView(downloadItem)
    }
}