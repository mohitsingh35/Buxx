package com.ncs.tradezy

import com.ncs.tradezy.repository.RealTimeUserResponse

data class UserState(
    val item:List<RealTimeUserResponse> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)