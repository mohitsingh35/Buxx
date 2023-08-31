package com.ncs.tradezy.repository

data class RealTimeUserResponse(
    val item: RealTimeUsers?,
    val key:String?="",

    ){

    data class RealTimeUsers(
        val userId:String?="",
        val name:String?="",
        val phNumber: String?="",
        val profileDPurl:String?="",
        val email:String?="",
        val fcmToken:String?=""
    )

}

