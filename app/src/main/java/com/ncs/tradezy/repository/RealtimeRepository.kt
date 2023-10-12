package com.ncs.tradezy.repository

import android.net.Uri
import com.ncs.tradezy.ResultState
import com.ncs.tradezy.AdContent
import com.ncs.tradezy.BuyerLocation
import com.ncs.tradezy.EachAdResponse
import com.ncs.tradezy.ImageMessage
import com.ncs.tradezy.MessageResponse
import com.ncs.tradezy.NotificationContent
import com.ncs.tradezy.PromotionalNotification
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

    fun updateADstatus(
        res: AdContent
    ):Flow<ResultState<String>>

    fun deleteAd(
        key:String
    ):Flow<ResultState<String>>
    fun insertNotification(
        item: NotificationContent.NotificationItem
    ): Flow<ResultState<String>>

    fun getNotification():Flow<ResultState<List<NotificationContent>>>
    fun getpromoNotification():Flow<ResultState<List<NotificationContent>>>

    fun updateNotification(
        res: NotificationContent
    ):Flow<ResultState<String>>

    fun updatePromoNoti(
        res: NotificationContent
    ):Flow<ResultState<String>>

    fun insertMessage(
        item: MessageResponse.MessageItems
    ): Flow<ResultState<String>>

    fun insertImages(
        images: List<Uri>,
        otherdetails: MessageResponse.MessageItems
    ): Flow<ResultState<String>>
    fun getMessage():Flow<ResultState<List<MessageResponse>>>
    fun getbuyer():Flow<ResultState<List<BuyerLocation>>>


    fun updateMessage(
        res: MessageResponse
    ):Flow<ResultState<String>>





}