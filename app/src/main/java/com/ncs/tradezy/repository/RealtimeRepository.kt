package com.ncs.tradezy.repository

import android.net.Uri
import com.ncs.tradezy.ResultState
import com.ncs.tradezy.AdContent
import com.ncs.tradezy.EachAdResponse
import com.ncs.tradezy.NotificationContent
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

    fun getAd():Flow<ResultState<List<EachAdResponse>>>

    fun updateAd(
        res: AdContent
    ):Flow<ResultState<String>>
    fun insertNotification(
        item: NotificationContent.NotificationItem
    ): Flow<ResultState<String>>

    fun getNotification():Flow<ResultState<List<NotificationContent>>>

    fun updateNotification(
        res: NotificationContent
    ):Flow<ResultState<String>>








}