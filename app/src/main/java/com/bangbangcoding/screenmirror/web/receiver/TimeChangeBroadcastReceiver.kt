package com.bangbangcoding.screenmirror.web.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bangbangcoding.screenmirror.web.utils.AlarmUtil

class TimeChangeBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            AlarmUtil.saveAlarm(it)
        }
    }
}