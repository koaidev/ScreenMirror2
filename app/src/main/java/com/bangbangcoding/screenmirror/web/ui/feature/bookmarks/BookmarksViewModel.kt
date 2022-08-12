package com.bangbangcoding.screenmirror.web.ui.feature.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangbangcoding.screenmirror.web.data.bookmark.BookmarkRepository
import com.bangbangcoding.screenmirror.web.data.local.Bookmark
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.di.module.MainScheduler
import com.bangbangcoding.screenmirror.web.di.module.NetworkScheduler
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
    private val bookmarkModel: BookmarkRepository,
    @DatabaseScheduler var databaseScheduler: Scheduler,
    @NetworkScheduler var networkScheduler: Scheduler,
    @MainScheduler var mainScheduler: Scheduler
) : ViewModel() {


    private var bookmarksSubscription: Disposable? = null
    private var bookmarkUpdateSubscription: Disposable? = null

    private val _isUpdateBookmarks = MutableLiveData<Boolean>()
    val isUpdateBookmarks: LiveData<Boolean> = _isUpdateBookmarks

    private val _isShowBookmarks = MutableLiveData<List<Bookmark>>()
    val isShowBookmarks: LiveData<List<Bookmark>> = _isShowBookmarks

    fun loadBookmarks(folder: String?) {
        bookmarksSubscription = bookmarkModel.getBookmarksFromFolderSorted(folder)
            .concatWith(Single.defer {
                if (folder == null) {
                    bookmarkModel.getFoldersSorted()
                } else {
                    Single.just(emptyList())
                }
            })
            .toList()
            .map { it.flatten() }
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { bookmarksAndFolders ->
//                uiModel.currentFolder = folder
                _isShowBookmarks.value = bookmarksAndFolders
            }
    }

    fun updateBookmarkIndicator(url: String) {
        bookmarkUpdateSubscription?.dispose()
        bookmarkUpdateSubscription = bookmarkModel.isBookmark(url)
            .subscribeOn(databaseScheduler)
            .observeOn(mainScheduler)
            .subscribe { boolean ->
                bookmarkUpdateSubscription = null
                _isUpdateBookmarks.value = boolean
            }
    }

    override fun onCleared() {
        bookmarksSubscription?.dispose()
        bookmarkUpdateSubscription?.dispose()
        super.onCleared()
    }

}