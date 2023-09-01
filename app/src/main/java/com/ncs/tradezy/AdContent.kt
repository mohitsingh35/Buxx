package com.ncs.tradezy

data class AdContent(
    val item: AdContentItem?,
    val key: String?="",

    ){
    data class AdContentItem(
        val title:String?="",
        val desc:String?="",
        val price:Int?=null,
        val time:Long?=null,
        val isExchangeable:String?="",
        val buyerLocation:String?="",
        val sellerId:String?="",
        val viewCount:String?="",
        val trendingViewCount:String?=""
    )
}
