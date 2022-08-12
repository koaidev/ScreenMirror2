package com.bangbangcoding.screenmirror.web.receiver

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.utils.AlarmUtil
import com.bangbangcoding.screenmirror.web.utils.Constants
import com.bangbangcoding.screenmirror.web.utils.PrefUtils


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            AlarmUtil.cancelAlarm(context)
            PrefUtils.putBoolean(context, Constants.Alarm.PREF_TIME_PUSH, false)
            AlarmUtil.saveAlarm(context)
            showPopup(context)
        }
    }

    private fun showPopup(context: Context) {
        AlarmUtil.sendNotification(context)
//        try {
//            (context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                newWakeLock(
//                    PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                    "MyApp::MyWakelockTag"
//                ).apply {
//                    acquire()
//                }
//            }
//        } catch (e: Exception) {
//        }
//
//        val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//
//        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
//        val isScreenOn = pm!!.isInteractive
//
//
//
//        when {
//            myKM.isKeyguardLocked || !isScreenOn || !foregrounded() -> {
//                AlarmUtil.sendNotification(context)
////                SettingContract.setNotificationVisible(context, true)
////                Log.d("ttt", "notify1111111111 ${myKM.isKeyguardLocked}, ${!isScreenOn} ")
////
////                when (SettingContract.loadReminderMode(context)) {
////                    ReminderMode.RING -> ringtoneDevice(context)
////                    else -> {
////                    }
////                }
////                SettingContract.saveDrinkPopup(context, false)
////                context.startActivity(Intent(context, GateWayActivity::class.java).apply {
////                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                })
////            }
////            !SettingContract.checkPauseMain(context) -> {
////                Log.d("ttt", "notify22222222")
////                context.startActivity(Intent(context, PopUpTopActivity::class.java).apply {
////                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                })
////            }
////            else -> {
////                Log.d("ttt", "notify 333333333")
////                SettingContract.saveDrinkPopup(context, false)
////                context.startActivity(Intent(context, GateWayActivity::class.java).apply {
////                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
////                })
//            }
//        }
    }

    private fun foregrounded(): Boolean {
        val appProcessInfo: ActivityManager.RunningAppProcessInfo =
            ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
    }

    private fun ringtoneDevice(context: Context) {
        val media = MediaPlayer.create(context, R.raw.drop1)
        media.setVolume(100f, 100f)
        media.start()
    }
}
