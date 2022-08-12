package com.bangbangcoding.screenmirror.web.ui.base

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.ui.CustomToast
import com.bangbangcoding.screenmirror.web.ui.dialog.InfoListDownloadBottomSheet
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadFileMain
import com.bangbangcoding.screenmirror.web.ui.paste.DownloadVideosMain
import com.bangbangcoding.screenmirror.web.ui.paste.GetLinkThroughWebView
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.progress.DownloadFragment
import com.bangbangcoding.screenmirror.web.utils.IntentActivity
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.ext.snackbarAction
import dagger.android.support.DaggerFragment
import java.util.ArrayList
import java.util.Random
import javax.inject.Inject

/**
 * Created by ThaoBKN on 25/11/2021
 */

abstract class BaseFragmentDownload : DaggerFragment() {

    lateinit var progressDialogGenaratinglink: ProgressDialog

    @Inject
    lateinit var userPreferences: UserPreferences

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (DownloadFileMain.downloadID == id) {
                requireActivity().snackbarAction(R.string.txt_download_completed, R.string.action_open) { _ ->
                    startActivity(
                        IntentActivity.getIntent(requireContext(), DownloadFragment::class.java)
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observerViewModel()
        requireActivity().registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(onDownloadComplete);
    }

    open fun observerViewModel() {}

    open fun setupUI() {}


    fun downloadVideo(url: String, condition1: String, condition2: String? = null, service: String, listener : DownloadVideosMain.OnDetectListener) {
        Log.e("ttt", "download url $url")
        if (url.isEmpty() && UrlUtils.checkURL(url)) {
            CustomToast.makeText(requireContext(), getString(R.string.enter_valid),
                CustomToast.SHORT, CustomToast.WARNING).show()
        } else if (!url.lowercase().contains(condition1)
                    && !url.lowercase().contains(condition2 ?: condition1)) {
            CustomToast.makeText(requireContext(), getString(R.string.enter_link_invalid_format, service),
                CustomToast.SHORT, CustomToast.WARNING).show()
        } else {
            val rand = Random()
            val randInt1 = rand.nextInt(2)
            Log.e("ttt", "random value = $randInt1")
            when {
                url.contains("myjosh.in") -> {
                    var myurl = url
                    myurl = myurl.substring(myurl.indexOf("http"))
                    myurl = myurl.substring(
                        myurl.indexOf("http://share.myjosh.in/"),
                        myurl.indexOf("Download Josh for more videos like this!")
                    )
//                    DownloadVideosMain.Start()
                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false, listener)
                    Log.e("downloadFileName12", url.trim())
                }
                url.contains("audiomack") ||
                        url.contains("zili") ||
                        url.contains("xhamster") ||
                        url.contains("zingmp3") ||
                        url.contains("vidlit") ||
                        url.contains("byte.co") ||
                        url.contains("fthis.gr") ||
                        url.contains("fw.tv") ||
                        url.contains("firework.tv") ||
                        url.contains("rumble") ||
                        url.contains("traileraddict")
                -> {
                    if (progressDialogGenaratinglink != null) {
                        progressDialogGenaratinglink.dismiss()
                    }

                    val intent = Intent(activity, GetLinkThroughWebView::class.java)
                    intent.putExtra("myurlis", url)
                    startActivityForResult(intent, 2)
                }


                url.contains("bemate") -> {
                    if (progressDialogGenaratinglink != null) {
                        progressDialogGenaratinglink.dismiss()
                    }
                    val urlq = url.substring(url.indexOf("https"), url.length)
                    val intent = Intent(activity, GetLinkThroughWebView::class.java)
                    intent.putExtra("myurlis", urlq)
                    startActivityForResult(intent, 2)
                }
                url.contains("chingari") -> {
                    var myurl = url
                    myurl = myurl.substring(
                        myurl.indexOf("https://chingari.io/"),
                        myurl.indexOf("For more such entertaining")
                    )
                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false, listener)
                    Log.e("downloadFileName12", myurl.trim())
                }
                url.contains("sck.io") || url.contains("snackvideo") -> {
                    var myurl = url
                    try {
                        if (myurl.length > 30) {
                            myurl = myurl.substring(
                                myurl.indexOf("http"),
                                myurl.indexOf("Click this")
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("ttt", "downloadVideo exception: ", e)
                    }
                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false, listener)
                    Log.e("downloadFileName12", myurl.trim())
                }
                else -> {
                    Log.d("ttt", "loadeed yet. $url")
                    var myUrl = url
                    try {
                        myUrl = myUrl.substring(myUrl.indexOf("http")).trim()
                    } catch (e: Exception) {
                        Log.e("ttt", "downloadVideo exception: ", e)
                    }

                    DownloadVideosMain.startDownload(this, requireContext(), myUrl, false, listener)
                }
            }
        }
    }

    fun checkInstaLogin(url:String, condition1: String, condition2: String?=null, service: String, listener: DownloadVideosMain.OnLoginInstagListener) {
        if (url.isEmpty() && UrlUtils.checkURL(url)) {
            CustomToast.makeText(requireContext(), getString(R.string.enter_valid),
                CustomToast.SHORT, CustomToast.WARNING).show()
            return;
        } else if (!url.lowercase().contains(condition1)
            && !url.lowercase().contains(condition2 ?: condition1)) {
            CustomToast.makeText(requireContext(), getString(R.string.enter_link_invalid_format, service),
                CustomToast.SHORT, CustomToast.WARNING).show()
            return;
        }
        DownloadVideosMain.checInsta(url, listener);
    }

    fun showBottomSheetDownloadVideos(videos: List<VideoInfo>?) {
        videos?.let {
            if (it.isNotEmpty()) {
                val downloadBottomSheet = InfoListDownloadBottomSheet.newInstance(
                    it[0].title ?: "Video_${System.currentTimeMillis()}",
                    it[0].duration ?: "00:00",
                    it[0].thumbnail ?: "",
                    ArrayList(it)
                )
                downloadBottomSheet.mListener = object :
                    InfoListDownloadBottomSheet.DownloadBottomSheetListener {
                    override fun onDownload(video: VideoInfo) {
                        Log.e("ttt", "handleDownloadVideoEvent: url = ${video.url}  --- title = ${video.title}${video.ext}")
                        DownloadFileMain.startDownloadingWithWifi(
                            context,
                            video.url,
                            video.title,
                            video.ext,
                            userPreferences
                        )
                        downloadBottomSheet.dismiss()
                    }
                }
                downloadBottomSheet.show(parentFragmentManager, "aaa")
            }
        }
    }
}