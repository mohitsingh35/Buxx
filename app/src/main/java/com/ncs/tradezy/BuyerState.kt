package com.ncs.tradezy

import com.ncs.tradezy.repository.RealTimeUserResponse

data class BuyerState(
    val item:List<BuyerLocation> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)