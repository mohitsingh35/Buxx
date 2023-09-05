package com.ncs.tradezy

data class MessageResponse(
    val item: MessageItems?,
    val key:String?="",

    ){

    data class MessageItems(
        val senderId:String?="",
        val receiverId:String?="",
        val message:String?="",
        val category:String?="",
        val read:String?="",
        val time:Long?=null,
        val ad:EachAdResponse?=null,
    )

}