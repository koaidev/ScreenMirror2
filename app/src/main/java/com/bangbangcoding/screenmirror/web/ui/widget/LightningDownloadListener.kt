package com.bangbangcoding.screenmirror.web.ui.widget

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.text.format.Formatter
import android.util.Log
import android.webkit.DownloadListener
import androidx.appcompat.app.AlertDialog
import com.anthonycr.grant.PermissionsManager
import com.anthonycr.grant.PermissionsResultAction
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.downloads.DownloadsRepository
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
//import com.highsecure.videodownloader.download.DownloadHandler
import com.bangbangcoding.screenmirror.web.logger.Logger
import com.bangbangcoding.screenmirror.web.ui.dialog.BrowserDialog.setDialogSize
import com.bangbangcoding.screenmirror.web.ui.preference.UserPreferences
import com.bangbangcoding.screenmirror.web.ui.studio.DownloadHandler
import io.reactivex.Scheduler
import javax.inject.Inject

class LightningDownloadListener @Inject constructor(
    private val activity: Activity
) : DownloadListener {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var downloadsRepository: DownloadsRepository

    @Inject
    lateinit var downloadHandler: DownloadHandler

    @Inject
    @DatabaseScheduler
    lateinit var databaseScheduler: Scheduler

    @Inject
    @NetworkScheduler
    lateinit var networkScheduler: Scheduler

    @Inject
    @MainScheduler
    lateinit var mainScheduler: Scheduler

    @Inject
    lateinit var logger: Logger

    private val TAG = "LightningDownloader"

    override fun onDownloadStart(
        url: String, name: String,
        contentDisposition: String?, mimetype: String?, contentLength: Long
    ) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
            object : PermissionsResultAction() {
                override fun onGranted() {
                    //val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                    val downloadSize: String = if (contentLength > 0) {
                        Formatter.formatFileSize(activity, contentLength)
                    } else {
                        activity.getString(R.string.unknown_size)
                    }
                    downloadHandler =  DownloadHandler()
                    val dialogClickListener =
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> downloadHandler.onDownloadStart(
                                    activity,
//                                    userPreferences,
                                    url,
//                                    userAgent,
//                                    contentDisposition,
//                                    mimetype,
//                                    downloadSize
                                    name
                                )
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                    val builder = AlertDialog.Builder(
                        activity
                    ) // dialog
                    val message = activity.getString(R.string.dialog_download)
                    val dialog: Dialog = builder.setTitle(name)
                        .setMessage(message)
                        .setPositiveButton(
                            activity.resources.getString(R.string.action_downloads),
                            dialogClickListener
                        )
                        .setNegativeButton(
                            activity.resources.getString(R.string.txt_cancel),
                            dialogClickListener
                        ).show()
                    setDialogSize(activity, dialog)
                    try {
                        logger.log(TAG, "Downloading: $name")
                    }catch (e: Exception){
                        Log.e("Error", e.toString())
                    }

                }

                override fun onDenied(permission: String) {
                    //no-op
                }
            })
    }
}