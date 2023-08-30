package com.ncs.tradezy.googleAuth

data class GoogleSignInState(
    val isSignInSuccessful:Boolean=false,
    val signInError:String?=null
)
