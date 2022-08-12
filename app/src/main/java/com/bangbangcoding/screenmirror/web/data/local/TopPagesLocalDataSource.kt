package com.bangbangcoding.screenmirror.web.data.local

import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.data.local.model.Payload
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopPagesLocalDataSource @Inject constructor(

) : TopPagesRepository {

    override fun getTopPages(): Observable<List<DomainAllow>> {
        val list = listOf(
            DomainAllow(
                id = 1,
                domain = "",
                payload = Payload(name = "Add", iconRes = R.drawable.ic_add),
                canEdit = false
            ),

            DomainAllow(
                id = 1,
                domain = "facebook.com",
                payload = Payload(name = "Facebook", iconRes = R.drawable.ic_home_facebook),
                canEdit = false
            ),

            DomainAllow(
                id = 1,
                domain = "https://www.youtube.com/",
                payload = Payload(name = "Youtube", iconRes = R.drawable.ic_youtube),
                canEdit = false
            ),
            DomainAllow(
                id = 1,
                domain = "https://www.tiktok.com/",
                payload = Payload(name = "TikTok", iconRes = R.drawable.ic_home_tiktok),
                canEdit = false
            ),
        )

        return Observable.just(list)
    }


    override fun getBlockList(): Observable<List<DomainAllow>> {
        return Observable.just(listOf())
    }
}