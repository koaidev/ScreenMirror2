package com.bangbangcoding.screenmirror.web.data.allowlist

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * The interface used to communicate with the ad block whitelist interface.
 */
interface AdBlockAllowListRepository {

    /**
     * Returns a [Single] that emits a list of all [AllowListItem] in the database.
     */
    fun allAllowListItems(): Single<List<AllowListItem>>

    /**
     * Returns a [Maybe] that emits the [AllowListItem] associated with the [url] if there is one.
     */
    fun allowListItemForUrl(url: String): Maybe<AllowListItem>

    /**
     * Returns a [Completable] that adds a [AllowListItem] to the database and completes when done.
     */
    fun addAllowListItem(whitelistItem: AllowListItem): Completable

    /**
     * Returns a [Completable] that removes a [AllowListItem] from the database and completes when
     * done.
     */
    fun removeAllowListItem(whitelistItem: AllowListItem): Completable

    /**
     * Returns a [Completable] that clears the entire database.
     */
    fun clearAllowList(): Completable
}
