package com.bangbangcoding.screenmirror.web.data.remote

import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.data.remote.service.ConfigService
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopPagesRemoteDataSource @Inject constructor(
    private val configService: ConfigService
) : TopPagesRepository {

    override fun getTopPages(): Observable<List<DomainAllow>> {
        return configService.getDomain()
    }

    override fun getBlockList(): Observable<List<DomainAllow>> {
        return configService.getBlockDomain()
    }

//    fun addFeedback(request : FeedbackRequest) {
//        configService.addFeedback(request.createRequest())
//    }
}