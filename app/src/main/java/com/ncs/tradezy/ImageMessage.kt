package com.ncs.tradezy

import android.net.Uri

data class ImageMessage(
    val item: ImageItems?,
    val key:String?="",

    ){

    data class ImageItems(
        val images:List<Uri>
    )

}