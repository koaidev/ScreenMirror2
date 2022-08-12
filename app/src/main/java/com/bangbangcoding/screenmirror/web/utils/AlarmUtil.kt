package com.bangbangcoding.screenmirror.web.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.receiver.AlarmReceiver
import com.bangbangcoding.screenmirror.web.ui.WebActivity
import java.util.*
import kotlin.math.ceil

object AlarmUtil {

    private const val ID_ALARM = 1
    const val NOTIFY_ID_ALARM = 987

    fun cancelAlarm(context: Context) {
        val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                ID_ALARM,
                intent,
                (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )
        }
        alarmMgr.cancel(alarmIntent)
    }

    fun sendNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendOreoNotification(
                context
            )
        } else {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifyIntent = Intent(context, WebActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(Constants.Alarm.PREF_TIME_PUSH, false)
            }

            val notifyPendingIntent = PendingIntent.getActivity(
                context,
                0,
                notifyIntent,
                (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )

            val drinkIntent = Intent(context, WebActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(Constants.Alarm.PREF_TIME_PUSH, true)
            }

            val drinkPendingIntent = PendingIntent.getActivity(
                context,
                0,
                drinkIntent,
                (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            )

            val notificationLayout = RemoteViews(context.packageName, R.layout.view_notification)

            notificationLayout.setOnClickPendingIntent(R.id.tvDrinkNotify, drinkPendingIntent)

            notificationLayout.setTextViewText(R.id.tvClock, getCurrentDate())

            val builder = NotificationCompat.Builder(context, "100")
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.drawable.ic_download_white_24dp)
//                .setCustomContentView(notificationLayout)
                .setContent(notificationLayout)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                builder.setAutoCancel(true)
                notificationManager.notify(NOTIFY_ID_ALARM, builder.build())
            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        Log.e("ttt", "getCurrentDate: ${calendar.timeInMillis}")
        return "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
    }

    private fun sendOreoNotification(
        context: Context
    ) {
        val random1 = Random()
        val j = random1.nextInt(5)
        val intent = Intent(context, WebActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Constants.Alarm.PREF_TIME_PUSH, false)
        }
        val pendingIntent =
            PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT)

        val drinkIntent = Intent(context, WebActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.Alarm.PREF_TIME_PUSH, true)
        }

        val drinkPendingIntent = PendingIntent.getActivity(
            context,
            0,
            drinkIntent,
            (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        )


        val notificationLayout = RemoteViews(context.packageName, R.layout.view_notification)

        notificationLayout.setOnClickPendingIntent(R.id.tvDrinkNotify, drinkPendingIntent)

        notificationLayout.setTextViewText(R.id.tvClock, getCurrentDate())

        val oreoNotification = OreoNotification(context)
        val builder = oreoNotification.getOreoNotification(
            notificationLayout,
            pendingIntent,
            R.drawable.ic_download_white_24dp
        )
        val random = Random()
        val num = random.nextInt(5)
        oreoNotification.getManager()!!.notify(NOTIFY_ID_ALARM, builder.build())
    }

    private fun createNotificationChannel(
        context: Context,
        notificationManager: NotificationManager
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("100", name, importance).apply {
                description = descriptionText
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun saveAlarm(context: Context, isCheckLoad: Boolean = false) {
        cancelAlarm(context)
        val hour = 11
        val minute = 0

        val calendar = Calendar.getInstance()
        val calendar1 = Calendar.getInstance()
        calendar1.set(Calendar.HOUR_OF_DAY, hour)
        calendar1.set(Calendar.MINUTE, minute)
        calendar1.set(Calendar.SECOND, 0)
        calendar1.set(Calendar.MILLISECOND, 0)

        val currentDate = calendar.get(Calendar.DATE)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)


        Log.e(
            "ttt",
            "saveAlarm: $currentDate :: $currentHour :: $currentMinute :: ${calendar.timeInMillis}"
        )

        if (calendar.timeInMillis > calendar1.timeInMillis) {
            calendar.set(Calendar.DATE, currentDate + 1)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
        }

        Log.e("ttt", "saveAlarm: calendar ${calendar.timeInMillis}")
        setAlarm(context, calendar.timeInMillis)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm(context: Context, time: Long) {
        val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    context,
                    ID_ALARM,
                    intent,
                    (PendingIntent.FLAG_IMMUTABLE)
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    ID_ALARM,
                    intent,
                    (PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmIntent)
        } else {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(time, alarmIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, alarmIntent)
        }
    }

    fun checkNextTimeDrinkNew(wk: Long, sl: Long, interval: Long, ht: Long): Long {
        if (sl == wk) {
            val t = ceil(((ht - wk).toFloat() / interval).toDouble()).toInt()
            return wk + (interval * t)
        } else if (sl < wk) {
            if (ht in (sl + 1) until wk) {
                return wk
            }
            if (ht >= wk) {
                val t = ceil(((ht - wk).toFloat() / interval).toDouble()).toInt()
                return wk + (interval * t)
            }

            if (ht <= sl) {
                val t = ceil(((ht - wk).toFloat() / interval).toDouble()).toInt()
                return wk + (interval * t)
            }
        } else {
            if (ht in (wk + 1) until sl) {
                val t = ceil(((ht - wk).toFloat() / interval).toDouble()).toInt()
                if (wk + (interval * t) > sl) {
                    return wk + (24 * 60 * 60 * 1000L)
                }
                return wk + (interval * t)
            }
            if (ht <= wk) {
                return wk
            }

            if (ht >= sl) {
                return wk + (24 * 60 * 60 * 1000L)
            }

        }
        return 0L
    }

    fun cancelNotification(ctx: Context, notifyId: Int) {
        val ns = Context.NOTIFICATION_SERVICE
        val nMgr = ctx.getSystemService(ns) as NotificationManager
        nMgr.cancel(notifyId)
    }
}
