package com.bangbangcoding.screenmirror.web.ui.feature.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.web.data.history.HistoryRepository
import com.bangbangcoding.screenmirror.web.data.local.HistoryEntry
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.utils.add
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    @DatabaseScheduler var databaseScheduler: Scheduler,
    @NetworkScheduler var networkScheduler: Scheduler,
    @MainScheduler var mainScheduler: Scheduler
) : BaseViewModel() {

    private var historyDisposable: Disposable? = null


    private val _listHistory = MutableLiveData<List<HistoryEntry>>()
    val listHistory: LiveData<List<HistoryEntry>> = _listHistory

    private val _isComplete = MutableLiveData<Boolean>()
    val isComplete: LiveData<Boolean> = _isComplete

    override fun start() {
        historyDisposable = historyRepository.lastHundredVisitedHistoryEntries()
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { list ->
                _listHistory.value = list
            }
    }

    override fun stop() {
        historyDisposable?.dispose()
    }

    fun deleteAllHistory() {
        historyRepository.deleteHistory()
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe {
                _isComplete.value = true
            }.add(this)
    }
}