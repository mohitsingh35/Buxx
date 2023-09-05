package com.ncs.tradezy


data class MessageState(

val item:List<MessageResponse> = emptyList(),
val error:String = "",
val isLoading:Boolean=false

)
