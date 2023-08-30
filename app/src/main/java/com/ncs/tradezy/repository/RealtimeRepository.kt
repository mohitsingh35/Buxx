package com.ncs.tradezy.repository

import android.net.Uri
import com.ncs.tradezy.ResultState
import com.ncs.tradezy.AdContent
import kotlinx.coroutines.flow.Flow


interface RealtimeRepository {

    fun insertuser(
        item: RealTimeUserResponse.RealTimeUsers
    ): Flow<ResultState<String>>
    fun getUser():Flow<ResultState<List<RealTimeUserResponse>>>
    fun update(
        res: RealTimeUserResponse
    ):Flow<ResultState<String>>
    fun insertAd(
        item: AdContent.AdContentItem, images:List<Uri>
    ): Flow<ResultState<String>>

    fun getAd():Flow<ResultState<List<AdContent>>>







}