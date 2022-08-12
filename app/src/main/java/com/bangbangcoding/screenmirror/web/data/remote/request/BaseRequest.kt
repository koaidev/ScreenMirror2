package com.bangbangcoding.screenmirror.web.data.remote.request

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

interface BaseRequest {
    fun createRequest(): RequestBody {
        val json = Gson().toJson(this)
        Log.e("GenerateRequestBody", "createRequestBody: $json")
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }
}