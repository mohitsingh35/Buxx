package com.ncs.tradezy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ncs.tradezy.ui.theme.background
import kotlinx.coroutines.delay

@Composable
fun loading(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}
@Composable
fun mainLoading() {
    val quotes = listOf("The more you share, the more you have", "In a world of 'use and toss,' be a 'reuse and sparkle' kind of person.", "Borrow, lend, repeat.", "Sell it : because your stuff wants to see the world, too", "Negotiations? What's the lowest you'll go?",
        "Selling items is like trying to find a date – it's all about the right profile picture",
        "Why buy when you can borrow",
        "Making life a little easier, one item at a time",
        )
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }
    var currentQuote by remember { mutableStateOf(quotes.random()) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentQuote = quotes.random()
        }
    }

    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = currentQuote,
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun internet() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.internet_error)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Sorry, we couldn't reach our servers", color = Color.LightGray, fontSize = 15.sp)
        Text(text = "Please check your Internet", color = Color.LightGray, fontSize = 15.sp)

    }
}
@Composable
fun emptyscreen() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(0.15f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.empty)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Text(text = "Oh no! Nothing here", color = Color.LightGray, fontSize = 15.sp)

    }
}

@Composable
fun maintenance() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.maintenance)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Sorry, we are undergoing maintenance", color = Color.LightGray, fontSize = 15.sp)
        Text(text = "We will be back soon", color = Color.LightGray, fontSize = 15.sp)

    }
}
@Composable
fun loadingdialog(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.smallloading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Hold On",)
                }
            }
        }
    })

}
@Composable
fun msgDialog(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.req)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Hold On",)
                }
            }
        }
    })

}
@Composable
fun loadingdialog2(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.smallloading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = {}, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
            }
        }
    })

}
@Composable
fun imagesendLoading() {
    val quotes = listOf("The more you share, the more you have", "In a world of 'use and toss,' be a 'reuse and sparkle' kind of person.", "Borrow, lend, repeat.", "Sell it : because your stuff wants to see the world, too", "Negotiations? What's the lowest you'll go?",
        "Selling items is like trying to find a date – it's all about the right profile picture",
        "Why buy when you can borrow",
        "Making life a little easier, one item at a time",
    )
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }
    var currentQuote by remember { mutableStateOf(quotes.random()) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.imagesend)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentQuote = quotes.random()
        }
    }

    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = currentQuote,
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}