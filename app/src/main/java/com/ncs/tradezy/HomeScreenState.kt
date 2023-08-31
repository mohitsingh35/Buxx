package com.ncs.tradezy

import com.ncs.tradezy.repository.RealTimeUserResponse

data class HomeScreenState(
    val item:List<EachAdResponse> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)