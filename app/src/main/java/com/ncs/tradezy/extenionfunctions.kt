package com.ncs.tradezy

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.LayoutDirection
import androidx.compose.animation.core.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row

import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.ncs.tradezy.ui.theme.background
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import androidx.compose.foundation.layout.Box as Box1

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
@RequiresApi(Build.VERSION_CODES.O)
fun convertLongToTime(timeInMillis: Long, pattern: String="hh:mm a"): String {
    val instant = Instant.ofEpochMilli(timeInMillis)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}
@RequiresApi(Build.VERSION_CODES.O)
fun convertLongToDate(timeInMillis: Long, pattern: String = "yyyy-MM-dd"): String {
    val instant = Instant.ofEpochMilli(timeInMillis)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}
@Composable
fun TextAnimation(next:String,initial:String,counter:Int) {
    var showNext by remember { mutableStateOf(true) }
    var textcount by remember { mutableStateOf(counter) }
    if (textcount==counter){
        LaunchedEffect(showNext) {
            delay(3000)
            showNext = !showNext
            textcount++
        }
    }


    Column() {
        Crossfade(targetState = showNext) { isNext ->
            Text(
                text = if (isNext) next else initial,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
    }
}

@Composable
fun showImageUpload(){
    Box1(modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.Center){
        Column {
            Box1(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
            Box1(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text(text = "Please Wait! Sending Images")
            }
        }
    }
}
class TriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = Path().apply {
            moveTo(x = 0f, y = size.height-offset)
            lineTo(x = 0f, y = size.height)
            lineTo(x = 0f + offset, y = size.height)
        }
        return Outline.Generic(path = trianglePath)
    }
}


