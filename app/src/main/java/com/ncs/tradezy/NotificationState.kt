package com.ncs.tradezy

import com.ncs.tradezy.repository.RealTimeUserResponse

data class NotificationState(
    val item:List<NotificationContent> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)
data class promoNotificationState(
    val item:List<PromotionalNotification> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)