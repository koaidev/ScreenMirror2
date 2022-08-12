package com.bangbangcoding.screenmirror.web.utils

import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import io.reactivex.disposables.Disposable

// ---------- RX JAVA ---------- //
fun Disposable.add(baseViewModel: BaseViewModel) {
    baseViewModel.addDisposable(this)
}