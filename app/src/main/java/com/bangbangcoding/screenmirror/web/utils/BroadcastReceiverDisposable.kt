package com.bangbangcoding.screenmirror.web.utils

import android.content.BroadcastReceiver
import android.content.Context
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

class BroadcastReceiverDisposable(
    private val context: Context,
    private val broadcastReceiver: BroadcastReceiver
) : Disposable {

    private val disposed = AtomicBoolean(false)

    override fun isDisposed(): Boolean = disposed.get()

    override fun dispose() {
        if (!disposed.getAndSet(true)) {
            context.unregisterReceiver(broadcastReceiver)
        }
    }
}
