package com.bangbangcoding.screenmirror.web.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.widget.RemoteViews

class OreoNotification constructor(context: Context) : ContextWrapper(context) {
    private val CHANNEL_ID = "downloadvideo.oreo"
    private val CHANNEL_NAME = "downloadvideo"

    private var notificationManager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager()!!.createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun getOreoNotification(
        remotesView: RemoteViews,
        pendingIntent: PendingIntent?, icon: Int
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remotesView)
            .setSmallIcon(icon)
            .setAutoCancel(true)
    }
}