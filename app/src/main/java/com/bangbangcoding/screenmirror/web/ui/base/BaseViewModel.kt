package com.bangbangcoding.screenmirror.web.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by ThaoBKN on 25/11/2021
 */

abstract class BaseViewModel : ViewModel() {

    abstract fun start()

    abstract fun stop()

    private var compositeDisposable: CompositeDisposable? = null

//    var loading = MutableLiveData<LoadingState>()

    /**
     * Can use [uiStateV2] to set/post value instead of [messageV2]
     */
//    protected val messageV2 = SingleLiveData<MessageUIState>()
//    protected val uiStateV2 = SingleLiveData<UIState>()
//
//    internal fun getAllUIStateLiveData(): LiveData<UIState> {
//        return MediatorLiveData<UIState>().apply {
//            addSource(messageV2) { value = it }
//            addSource(uiStateV2) { value = it }
//        }
//    }

    fun addDisposable(disposable: Disposable) {
        if (compositeDisposable == null || compositeDisposable?.isDisposed == true) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }

    fun dispose() {
        compositeDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
    }
}

//internal fun BaseViewModel.observe(lifecycleOwner: LifecycleOwner, baseView: BaseView) {
//    getAllUIStateLiveData().observe(
//        lifecycleOwner,
//        { uiState ->
//            baseView.observeAllUIState(uiState)
//        }
//    )
//}