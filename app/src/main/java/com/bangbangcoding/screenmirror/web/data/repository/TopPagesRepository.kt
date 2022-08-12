package com.bangbangcoding.screenmirror.web.data.repository

import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.web.di.qualifier.LocalData
import com.bangbangcoding.screenmirror.web.di.qualifier.RemoteData
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface TopPagesRepository {
    fun getTopPages(): Observable<List<DomainAllow>>

    fun getBlockList(): Observable<List<DomainAllow>>
}

@Singleton
class TopPagesRepositoryImpl @Inject constructor(
    @LocalData private val localDataSource: TopPagesRepository,
    @RemoteData private val remoteDataSource: TopPagesRepository
) : TopPagesRepository {

    override fun getTopPages(): Observable<List<DomainAllow>> {
        return localDataSource.getTopPages()
//        return remoteDataSource.getTopPages()
    }


    override fun getBlockList(): Observable<List<DomainAllow>> {
        return remoteDataSource.getBlockList()
    }
}