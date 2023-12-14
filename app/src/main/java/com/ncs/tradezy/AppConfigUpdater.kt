package com.ncs.tradezy

data class AppConfigUpdater(
    val logs:String?="",
    val url:String?="",
    val version:String?="",
    val forceUpdate:Boolean=false,
)
