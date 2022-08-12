package com.bangbangcoding.screenmirror.web.ui.feature.bookmarks

import com.bangbangcoding.screenmirror.web.data.local.Bookmark

interface BookmarksView {

    fun navigateBack()

    fun handleUpdatedUrl(url: String)

    fun handleBookmarkDeleted(bookmark: Bookmark)

    fun reloadData()
}
