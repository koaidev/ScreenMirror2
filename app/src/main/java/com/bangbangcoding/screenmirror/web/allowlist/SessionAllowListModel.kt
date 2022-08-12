package com.bangbangcoding.screenmirror.web.allowlist

import android.annotation.SuppressLint
import androidx.core.net.toUri
import com.bangbangcoding.screenmirror.web.data.allowlist.AdBlockAllowListRepository
import com.bangbangcoding.screenmirror.web.data.allowlist.AllowListItem
import com.bangbangcoding.screenmirror.web.di.module.DatabaseScheduler
import com.bangbangcoding.screenmirror.web.logger.Logger
import io.reactivex.Completable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("CheckResult")
@Singleton
class SessionAllowListModel @Inject constructor(
    private val adBlockAllowListModel: AdBlockAllowListRepository,
    @DatabaseScheduler private val ioScheduler: Scheduler,
    private val logger: Logger
) : AllowListModel {

    private var whitelistSet = hashSetOf<String>()

    init {
        adBlockAllowListModel
            .allAllowListItems()
            .map { it.map(AllowListItem::url).toHashSet() }
            .subscribeOn(ioScheduler)
            .subscribe { hashSet -> whitelistSet = hashSet }
    }

    override fun isUrlAllowedAds(url: String): Boolean =
        url.toUri().host?.let(whitelistSet::contains) ?: false

    override fun addUrlToAllowList(url: String) {
        url.toUri().host?.let { host ->
            adBlockAllowListModel
                .allowListItemForUrl(host)
                .isEmpty
                .flatMapCompletable {
                    if (it) {
                        adBlockAllowListModel.addAllowListItem(
                            AllowListItem(host, System.currentTimeMillis())
                        )
                    } else {
                        Completable.complete()
                    }
                }
                .subscribeOn(ioScheduler)
                .subscribe { logger.log(TAG, "whitelist item added to database") }

            whitelistSet.add(host)
        }
    }

    override fun removeUrlFromAllowList(url: String) {
        url.toUri().host?.let { host ->
            adBlockAllowListModel
                .allowListItemForUrl(host)
                .flatMapCompletable(adBlockAllowListModel::removeAllowListItem)
                .subscribeOn(ioScheduler)
                .subscribe { logger.log(TAG, "whitelist item removed from database") }

            whitelistSet.remove(host)
        }
    }

    companion object {
        private const val TAG = "SessionAllowListModel"
    }
}
