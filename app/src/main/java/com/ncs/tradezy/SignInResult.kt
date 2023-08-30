package com.ncs.tradezy

data class SignInResult(
    val data: UserData?,
    val errorMessage:String?
)
data class UserData(
    val userID:String,
    val username:String?,
    val profilePictureUrl:String?,
    val email:String?,
    val phNum:String?,
)
