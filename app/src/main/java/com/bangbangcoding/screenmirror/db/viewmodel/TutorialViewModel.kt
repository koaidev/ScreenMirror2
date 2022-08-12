package com.bangbangcoding.screenmirror.db.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TutorialViewModel : ViewModel() {
    val currentPage = MutableLiveData<Int>()
    init{
        currentPage.value = 0
    }
}