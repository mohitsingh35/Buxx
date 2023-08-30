package com.ncs.tradezy



data class AdState(
    val item:List<AdContent> = emptyList(),
    val error:String = "",
    val isLoading:Boolean=false
)
