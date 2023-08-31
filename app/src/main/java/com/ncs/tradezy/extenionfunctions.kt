package com.ncs.tradezy

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Context.showMsg(
    msg:String,
    duration: Int= Toast.LENGTH_SHORT
)= if (msg!="") {
    Toast.makeText(this,msg,duration).show()
} else {

}

@RequiresApi(Build.VERSION_CODES.O)
fun convertLongToTimeString(timeInMillis: Long, pattern: String="yyyy-MM-dd HH:mm:ss"): String {

    val instant = Instant.ofEpochMilli(timeInMillis)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}
