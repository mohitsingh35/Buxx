package com.ncs.tradezy

data class BuyerLocation(
    val item: BuyerLocationItem?,
    val key:String?="",

    ){

    data class BuyerLocationItem(
        val list:String?="",
    )

}