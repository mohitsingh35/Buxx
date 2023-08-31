package com.ncs.tradezy

data class AdContent(
    val item: AdContentItem?,
    val key: String?,

    ){
    data class AdContentItem(
        val title:String,
        val desc:String,
        val price:Int,
        val time:Long,
        val isExchangeable:String,
        val buyerLocation:String,
        val sellerId:String
    )
}
