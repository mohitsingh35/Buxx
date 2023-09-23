package com.ncs.tradezy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun adHost(item1:EachAdResponse,viewModel: ProfileActivityViewModel= hiltViewModel(),viewModel2: NotificationViewModel= hiltViewModel(),
           viewModel3:AddScreenViewModel= hiltViewModel(),viewModel4:HomeScreenViewModel= hiltViewModel(),
           viewModel5:ChatViewModel= hiltViewModel()){
    val scope= rememberCoroutineScope()
    val context= LocalContext.current

    var markassold by remember {
        mutableStateOf(false)
    }
    var buy by remember {
        mutableStateOf(false)
    }

    val res4=viewModel4.res.value
    var item:EachAdResponse=item1
    for (i in 0 until res4.item.size){
        if (res4.item[i].key==item1.key){
            item=res4.item[i]
        }
    }
    val sameuser by remember {
        mutableStateOf(item.item?.sellerId==FirebaseAuth.getInstance().currentUser?.uid)
    }

    val issold=item.item?.sold
    Log.d("isSold value",item.toString())
    if (markassold){
        markassold=false
        LaunchedEffect(key1 = true ){
            scope.launch(Dispatchers.Main) {
                viewModel3.updateADstatus(
                    AdContent(item = AdContent.AdContentItem(sold = "true"),key = item.key)
                ).collect{
                    when(it){
                        is ResultState.Success->{
                            context.showMsg(
                                msg="Marked As Sold"
                            )
                        }
                        is ResultState.Failure->{
                            context.showMsg(
                                msg=it.msg.toString()
                            )
                        }
                        ResultState.Loading->{
                        }
                    }
                }
            }
        }
    }
    val res=viewModel.res.value
    var seller:RealTimeUserResponse.RealTimeUsers?=null
    var buyer:RealTimeUserResponse.RealTimeUsers?=null
    for (i in 0 until res.item.size){
        if (res.item[i].item?.userId==item.item?.sellerId){
            seller=res.item[i].item
        }
    }
    for (i in 0 until res.item.size){
        if (res.item[i].item?.userId==FirebaseAuth.getInstance().currentUser?.uid){
            buyer=res.item[i].item
        }
    }

    var viewcount by remember {
        mutableStateOf(item.item?.viewCount?.toInt())
    }
    if (!sameuser){
        LaunchedEffect(key1 = true ){
            viewcount = viewcount!! + 1
        }
    }
    var trendingViewCount by remember {
        mutableStateOf(item.item?.trendingViewCount?.toInt())
    }
    if (!sameuser){
        LaunchedEffect(key1 = true) {
            delay(10000)
            trendingViewCount = trendingViewCount!! + 1
        }
    }
    var sendExchangeNotification by remember {
        mutableStateOf(false)
    }
    val  title="New Exchange Request"
    val message="Exchange Request from ${buyer?.name}. Tap here to know more."
    if (sendExchangeNotification){
        sendExchangeNotification=false
        sendNotification(PushNotification(NotificationData(title,message),seller?.fcmToken!!))
        //send message as well to firebase with notification

        LaunchedEffect(key1 = true){
            scope.launch(Dispatchers.Main) {
                viewModel2.insertNotification(
                    NotificationContent.NotificationItem
                        (title = title,message=message,time = System.currentTimeMillis(),
                        receiverID = seller.userId,senderID = buyer?.userId, read = "false",ad = item1)).collect {
                    when (it) {
                        is ResultState.Success -> {
                            context.showMsg(
                                msg = it.data
                            )
                        }

                        is ResultState.Failure -> {
                            context.showMsg(
                                msg = it.msg.toString()
                            )
                        }

                        ResultState.Loading -> {
                        }
                    }
                }
            }
        }
        LaunchedEffect(key1 = true){
            scope.launch(Dispatchers.Main) {
                viewModel5.insertMessage(
                    MessageResponse.MessageItems(senderId = FirebaseAuth.getInstance().currentUser?.uid,
                        receiverId = item.item?.sellerId,message=message,category = "Exchange",read = "false", ad = item, time = System.currentTimeMillis())).collect {
                    when (it) {
                        is ResultState.Success -> {
                            context.showMsg(
                                msg = it.data
                            )
                        }

                        is ResultState.Failure -> {
                            context.showMsg(
                                msg = it.msg.toString()
                            )
                        }

                        ResultState.Loading -> {
                        }
                    }
                }
            }
        }
    }
//    Box(modifier = Modifier
//        .fillMaxSize()
//        
//        .padding(16.dp)){
        Column(
            Modifier
                .fillMaxSize()
                .background(background)
        ) {
            LaunchedEffect(key1 = true ){
                delay(500L)
                scope.launch(Dispatchers.Main) {
                    viewModel3.update(
                        AdContent(item = AdContent.AdContentItem(viewCount = viewcount.toString(), trendingViewCount = trendingViewCount.toString()),key = item.key)
                    ).collect{
                        when(it){
                            is ResultState.Success->{
                                context.showMsg(
                                    msg=""
                                )
                            }
                            is ResultState.Failure->{
                                context.showMsg(
                                    msg=it.msg.toString()
                                )
                            }
                            ResultState.Loading->{
                            }
                        }
                    }
                }
            }
            LaunchedEffect(key1 = true ){
                delay(10500L)
                scope.launch(Dispatchers.Main) {
                    viewModel3.update(
                        AdContent(item = AdContent.AdContentItem(viewCount = viewcount.toString(), trendingViewCount = trendingViewCount.toString()),key = item.key)
                    ).collect{
                        when(it){
                            is ResultState.Success->{
                                context.showMsg(
                                    msg=""
                                )
                            }
                            is ResultState.Failure->{
                                context.showMsg(
                                    msg=it.msg.toString()
                                )
                            }
                            ResultState.Loading->{
                            }
                        }
                    }
                }
            }
//            Row(Modifier.fillMaxWidth()) {
//                Box(modifier = Modifier
//                    .padding(start = 28.dp, top = 30.dp)
//                    .clip(CircleShape)
//                    .clickable {
//                        context.startActivity(Intent(context, MainActivity::class.java))
//                    }) {
//                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
//                }
//            }
//            Spacer(modifier = Modifier.height(25.dp))
            LazyColumn {
                items(1){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)){

                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            items(item.item?.images?.size!!) { index ->
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clickable {
                                        val intent = Intent(context, ImageHostActivity::class.java)
                                        intent.putStringArrayListExtra(
                                            "images",
                                            ArrayList(item.item?.images!!)
                                        )
                                        intent.putExtra("sender", " ")
                                        intent.putExtra("time"," ")
                                        context.startActivity(intent)
                                    }
                                    .background(Color.White), contentAlignment = Alignment.Center){
                                    AsyncImage(model = item.item!!.images?.get(index), contentDescription =  " ",modifier = Modifier
                                        .padding(end = 5.dp, top = 10.dp)
                                        .width(380.dp), contentScale = ContentScale.Fit)

                                    Box(modifier = Modifier
                                        .height(300.dp)
                                        .width(380.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Transparent,
                                                    Color.Transparent,
                                                    Color.Transparent,
                                                    Color.LightGray
                                                )
                                            )
                                        ))
                                    Row(modifier = Modifier
                                        .width(320.dp)
                                        .fillMaxHeight()
                                        .padding(bottom = 20.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
                                        Box(
                                            modifier = Modifier
                                                .height(30.dp)
                                                .width(55.dp)
                                                .clip(
                                                    RoundedCornerShape(15.dp)
                                                )
                                                .background(Color.Black),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1} / ${item.item?.images?.size}",
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }



                            }

                        }
                        Box(modifier = Modifier
                            .height(80.dp)
                            .width(400.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        main,
                                        Color.Transparent
                                    )
                                )
                            ))
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (item.item?.price == 0) {
                            Text(text = "No Price!", color = Color.Black, fontSize = 25.sp,fontWeight = FontWeight.Bold
                            )
                        }
                        if (item.item?.price != 0) {
                            Text(
                                text = "₹ ${item.item?.price.toString()}",
                                color = Color.Black,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(text = convertLongToDate(item.item?.time!!), color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Thin)

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                        Text(text = "Title", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Bold
                        )
                    }
                    Box(modifier = Modifier.padding(start = 20.dp,end = 20.dp)) {
                        Text(text = item.item?.title!!, color = Color.Black, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(500.dp)
                        .background(Color.LightGray))
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 20.dp)) {
                        Text(text = "Description", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Bold
                        )
                    }
                    Box(modifier = Modifier.padding(start = 20.dp,end = 20.dp)) {
                        Text(text = item.item?.desc!!, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Thin)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(500.dp)
                        .background(Color.LightGray))
                    if (item.item?.tags?.isNotEmpty()!!){
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 20.dp)) {
                            Text(text = "Tags", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Bold
                            )
                        }
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 20.dp)) {
                            for (i in 0 until item.item?.tags?.size!! ){
                                eachtag(tag = item.item?.tags!![i]!!)
                            }
                            
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Box(modifier = Modifier
                            .height(1.dp)
                            .width(500.dp)
                            .background(Color.LightGray))
                        Spacer(modifier = Modifier.height(5.dp))
                        
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 20.dp)) {
                        Text(text = "Buyer Location", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.padding(start = 20.dp,end = 20.dp)) {
                        Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "", tint = Color.LightGray, modifier = Modifier.size(25.dp))
                        Text(text = item.item?.buyerLocation!!, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Thin)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(500.dp)
                        .background(Color.LightGray))
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp,end = 20.dp)) {
                        Text(text = "Seller", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .height(70.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = seller?.profileDPurl, contentDescription = "", modifier = Modifier
                            .size(55.dp)
                            .clip(
                                CircleShape
                            ))
                        Spacer(modifier = Modifier.width(15.dp))
                        Column {
                            Text(text = "Posted by", color = Color.Black, fontSize = 18.sp,fontWeight = FontWeight.Bold)
                            Text(text = "~ ${seller?.name}", color = Color.Black, fontSize = 14.sp,fontWeight = FontWeight.Light)

                        }

                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(500.dp)
                        .background(Color.LightGray))
                    Spacer(modifier = Modifier.height(5.dp))
                    if (!sameuser){
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(50.dp), horizontalArrangement = Arrangement.SpaceBetween){
                            Button(onClick = {  }) {
                                Text(text = "Buy")
                            }

                            if (item.item!!.exchangeable =="true"){
                                Button(onClick = {
                                    sendExchangeNotification=true
                                }) {
                                    Text(text = "Exchange")
                                }
                            }
                        }
                    }
                    if (sameuser && issold=="false"){
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(50.dp), horizontalArrangement = Arrangement.SpaceBetween){
                            Button(onClick = {
                                markassold=true
                            }) {
                                Text(text = "Mark as Sold")
                            }

                            Button(onClick = {
                                scope.launch(Dispatchers.Main) {

                                    viewModel4.delete(item.key!!).collect {
                                        when (it) {
                                            is ResultState.Success -> {
                                                context.showMsg(
                                                    msg = it.data
                                                )
                                                val intent = Intent(context, MainActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                context.startActivity(intent)                                                }
                                            is ResultState.Failure -> {
                                                context.showMsg(
                                                    msg = it.msg.toString()
                                                )
                                            }

                                            ResultState.Loading -> {
                                            }
                                        }
                                    }
                                }

                            }) {
                                Text(text = "Delete")
                            }

                        }
                    }
                    if(issold=="true" && sameuser){
                        Row {
                            Text(text = "Sold!", color = betterWhite, fontSize = 30.sp)
                        }
                    }


                }
            }
        }


//    }


}

@Composable
fun eachtag(tag:String){
    Box(modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(main), contentAlignment = Alignment.Center){
        Row(Modifier.padding(10.dp)) {
            Box(modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(Color.White))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = tag, fontSize = 14.sp, color = Color.Black)
        }
    }
}


private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful) {
            Log.d(TAG, "Response")
        } else {
            Log.e(TAG, response.errorBody().toString())
        }
    } catch(e: Exception) {
        Log.e(TAG, e.toString())
    }
}