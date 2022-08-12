package com.bangbangcoding.screenmirror.web.ui.progress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import javax.inject.Inject

class DashboardViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    override fun start() {
        //TODO("Not yet implemented")
    }

    override fun stop() {
        //TODO("Not yet implemented")
    }
}