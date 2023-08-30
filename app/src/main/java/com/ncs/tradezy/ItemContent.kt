package com.ncs.tradezy

import android.net.Uri

data class ItemContent(
    val image:List<Int>,
    val title:String,
    val desc:String,
    val price:Int,
    val time:Long,
    val isExchangeable:Boolean,
    val buyerLocation:String
)
