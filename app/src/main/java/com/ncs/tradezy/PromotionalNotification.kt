package com.ncs.tradezy

data class PromotionalNotification(
    val item: PromotionalNotificationItem?,
    val key: String?="",

    ){
    data class PromotionalNotificationItem(
        val title:String?="",
        val message:String?="",
        val time:Long?=null,
        val msgread:Map<String,String>?= emptyMap(),
        val senderurl:String?="",
        val sendername:String?=""
    )
}
