package com.bangbangcoding.screenmirror.web.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TaskRunner {
    private val executor: Executor =
        Executors.newSingleThreadExecutor() // change according to your requirements
    private val handler: Handler = Handler(Looper.getMainLooper())

    interface Callback<R> {
        fun onComplete(result: R)
    }

    fun <R> executeAsync(callable: Callable<R>, callback: Callback<R>) {
        executor.execute {
            val result: R = callable.call()
            handler.post { callback.onComplete(result) }
        }
    }
}

internal class LongRunningTask(private val input: List<String>) :
    Callable<List<Long>> {
    override fun call(): List<Long> {
        // Some long running task
        val result = arrayListOf<Long>()

        for (link in input) {
            val url = URL(link)
            val c = url.openConnection()
            c.connect()
            val lengthOfFile: Int = c.contentLength
            Log.e("ttt", "getContentLength $lengthOfFile")
            result.add(lengthOfFile.toLong())
        }
        return result
    }
}