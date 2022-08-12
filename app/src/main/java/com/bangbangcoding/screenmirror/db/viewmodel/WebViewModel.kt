package com.bangbangcoding.screenmirror.db.viewmodel

import androidx.lifecycle.*
import com.bangbangcoding.screenmirror.db.model.entity.History
import com.bangbangcoding.screenmirror.db.model.entity.Shortcut
import com.bangbangcoding.screenmirror.db.repo.WebRepository
import kotlinx.coroutines.launch

class WebViewModel(private val repository: WebRepository) : ViewModel() {
    val allShortcut: LiveData<List<Shortcut>> = repository.allShortcut.asLiveData()
    val allHistory: LiveData<List<History>> = repository.allHistory.asLiveData()
    val canGoBack: MutableLiveData<Boolean> = MutableLiveData()
    val canGoForward: MutableLiveData<Boolean> = MutableLiveData()
    val currentUrl = MutableLiveData<String>()
    val visibility = MutableLiveData<Boolean>()
    init {
        canGoForward.value = false
        canGoBack.value = false
        visibility.value = false
    }

    fun insertShortcut(shortcut: Shortcut) = viewModelScope.launch {
        repository.insertShortcut(shortcut)
    }

    fun deleteShortcut(shortcut: Shortcut) = viewModelScope.launch {
        repository.deleteShortcut(shortcut)
    }

    fun insertAllShortcut(vararg shortcut: Shortcut) = viewModelScope.launch {
        repository.insertAllShortcut(*shortcut)
    }

    fun insertHistory(history: History) = viewModelScope.launch {
        repository.insertHistory(history)
    }

    fun deleteAllHistory() = viewModelScope.launch {
        repository.deleteAllHistory()
    }

    fun deleteHistory(history: History) = viewModelScope.launch {
        repository.deleteHistory(history)
    }

}

class WebViewModelFactory(private val repository: WebRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WebViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}