package com.ncs.tradezy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun adHost(item1:EachAdResponse,viewModel: ProfileActivityViewModel= hiltViewModel(),viewModel2: NotificationViewModel= hiltViewModel(),
           viewModel3:AddScreenViewModel= hiltViewModel(),viewModel4:HomeScreenViewModel= hiltViewModel(),
           viewModel5:ChatViewModel= hiltViewModel(),onClick:(String)->Unit){
    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var markassold by remember {
        mutableStateOf(false)
    }
    var buy by remember {
        mutableStateOf(false)
    }
    var showexchangesheet by remember {
        mutableStateOf(false)
    }
    var showdeletesheet by remember {
        mutableStateOf(false)
    }
    var showmarkassoldsheet by remember {
        mutableStateOf(false)
    }
    var showLoadingDialog by remember {
        mutableStateOf(false)
    }
    var showmsgsentDialog by remember {
        mutableStateOf(false)
    }
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    var userList=ArrayList<String>()
    var isUserinDB by remember {
        mutableStateOf(false)
    }
    val profiles: ProfileActivityViewModel = hiltViewModel()
    val profilesres=profiles.res.value
    for (i in 0 until profilesres.item.size){
        userList.add(profilesres.item[i].item?.userId!!)
    }
    if (userList.contains(currentuser)){
        isUserinDB=true
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
    if (showLoadingDialog){
        loadingdialog()
    }
    if (showmsgsentDialog){
        msgDialog()
    }
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
                            showmarkassoldsheet=false
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
                                msg = ""
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
                                msg = "Message Sent"
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(background)

    ) { contentpadding ->

        Column(
            Modifier
                .fillMaxSize()
                .background(background)
        ) {
            Spacer(modifier = Modifier.padding(contentpadding))
            LaunchedEffect(key1 = true) {
                delay(500L)
                scope.launch(Dispatchers.Main) {
                    viewModel3.update(
                        AdContent(
                            item = AdContent.AdContentItem(
                                viewCount = viewcount.toString(),
                                trendingViewCount = trendingViewCount.toString()
                            ), key = item.key
                        )
                    ).collect {
                        when (it) {
                            is ResultState.Success -> {
                                context.showMsg(
                                    msg = ""
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
            LaunchedEffect(key1 = true) {
                delay(10500L)
                scope.launch(Dispatchers.Main) {
                    viewModel3.update(
                        AdContent(
                            item = AdContent.AdContentItem(
                                viewCount = viewcount.toString(),
                                trendingViewCount = trendingViewCount.toString()
                            ), key = item.key
                        )
                    ).collect {
                        when (it) {
                            is ResultState.Success -> {
                                context.showMsg(
                                    msg = ""
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
            Box() {
                LazyColumn {
                    items(1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
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
                                            val intent =
                                                Intent(context, ImageHostActivity::class.java)
                                            intent.putStringArrayListExtra(
                                                "images",
                                                ArrayList(item.item?.images!!)
                                            )
                                            intent.putExtra("sender", " ")
                                            intent.putExtra("time", " ")
                                            context.startActivity(intent)
                                        }
                                        .background(Color.White),
                                        contentAlignment = Alignment.Center) {
                                        AsyncImage(
                                            model = item.item!!.images?.get(index),
                                            contentDescription = " ",
                                            modifier = Modifier
                                                .padding(end = 5.dp, top = 10.dp)
                                                .width(380.dp),
                                            contentScale = ContentScale.Fit
                                        )

                                        Box(
                                            modifier = Modifier
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
                                                )
                                        )
                                        Row(
                                            modifier = Modifier
                                                .width(320.dp)
                                                .fillMaxHeight()
                                                .padding(bottom = 20.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
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
                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(400.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                main,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(start = 16.dp, top = 10.dp)){
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                        .clickable {
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    MainActivity::class.java
                                                )
                                            )
                                        }, contentAlignment = Alignment.Center
                                ){
                                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "",tint= betterWhite )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(start = 20.dp, top = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.item?.price == 0) {
                                Text(
                                    text = "No Price!",
                                    color = Color.Black,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
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

                            Text(
                                text = convertLongToDate(item.item?.time!!),
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Thin
                            )

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(500.dp)
                                .background(Color.LightGray)
                        )
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                            Text(
                                text = "Title",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                            Text(text = item.item?.title!!, color = Color.Black, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(500.dp)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                            Text(
                                text = "Description",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                            Text(
                                text = item.item?.desc!!,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Thin
                            )
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(500.dp)
                                .background(Color.LightGray)
                        )
                        if (item.item?.tags?.isNotEmpty()!!) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    top = 10.dp,
                                    end = 20.dp
                                )
                            ) {
                                Text(
                                    text = "Tags",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp,
                                        top = 10.dp,
                                        end = 20.dp
                                    )
                                    .fillMaxWidth()
                            ) {
                                FlowRow(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    for (i in 0 until item.item?.tags?.size!!) {
                                        eachtag(tag = item.item?.tags!![i]!!)
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Box(
                                modifier = Modifier
                                    .height(10.dp)
                                    .width(500.dp)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.height(5.dp))

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                            Text(
                                text = "Buyer Location (Preferred)",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "",
                                tint = Color.LightGray,
                                modifier = Modifier.size(25.dp)
                            )
                            Text(
                                text = item.item?.buyerLocation!!,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Thin
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(500.dp)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp)) {
                            Text(
                                text = "Seller",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp)
                                .height(70.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = seller?.profileDPurl,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Column {
                                Text(
                                    text = "Posted by",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "~ ${seller?.name}",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light
                                )

                            }

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Box(
                            modifier = Modifier
                                .height(10.dp)
                                .width(500.dp)
                                .background(Color.LightGray)
                        )


                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 25.dp), contentAlignment = Alignment.BottomCenter
                ) {


                            if (!sameuser) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = betterWhite,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(main)
                                            .clickable {
                                                showBottomSheet = true
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            Icon(
                                                painter = painterResource(id = R.drawable.buy_ic),
                                                contentDescription = "",Modifier.size(25.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = "Buy")
                                        }
                                    }
                                    if (item.item!!.exchangeable == "true") {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .height(50.dp)
                                                .background(Color.LightGray)
                                                .clickable {
                                                    showexchangesheet = true
                                                }, contentAlignment = Alignment.Center
                                        ) {
                                            Row {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.exchange_ic),
                                                    contentDescription = "", tint = betterWhite,
                                                    modifier = Modifier.size(25.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text = "Exchange", color = betterWhite)
                                            }
                                        }

                                    }
                                }
                            }
                            if (sameuser && issold == "false") {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .height(50.dp)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = betterWhite,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(main)
                                            .clickable {
                                                showmarkassoldsheet = true
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "", modifier = Modifier.size(25.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Box(modifier = Modifier.padding(top = 2.dp)) {
                                                Text(text = "Mark as Sold")

                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .clip(
                                                RoundedCornerShape(10.dp)
                                            )
                                            .height(50.dp)
                                            .background(Color.Red)
                                            .clickable {
                                                showdeletesheet = true
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        Row {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "",
                                                tint = betterWhite,modifier = Modifier.size(25.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(text = "Delete", color = betterWhite)

                                        }

                                    }
                                }
                            }
                            if (issold == "true" && sameuser) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp)
                                        .height(50.dp)
                                        .clip(
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = betterWhite,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(Color.Gray)
                                        .clickable {
                                            markassold = true
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Row {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "",
                                            tint = betterWhite
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = "Sold!", color = betterWhite)
                                    }
                                }



                    }
                }
            }
        }
        var price by remember { mutableStateOf(item.item?.price) }
        var showcover by remember {
            mutableStateOf(false)
        }
        var offerscreen by remember {
            mutableStateOf(true)
        }
        var sendbuyreq by remember {
            mutableStateOf(false)
        }
        var sendexcreq by remember {
            mutableStateOf(false)
        }
        var sendwhatsapp by remember {
            mutableStateOf(false)
        }
        var exccovermessage by remember {
            mutableStateOf("Hi, I am willing to offer exchange for your product : ${item.item?.title}, I want to exchange these items for your product... \n\nLooking for a positive response from your side!")
        }
//        if (sendwhatsapp){
//            val mobileNumber: String = item.item!!.whatsappNum!!
//            val message: String = exccovermessage
//            val installed = appInstalledOrNot("com.whatsapp",context)
//            if (installed) {
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data =
//                    Uri.parse("http://api.whatsapp.com/send?phone=+91$mobileNumber&text=$message")
//                context.startActivity(intent)
//            } else {
//                Toast.makeText(
//                    context,
//                    "Whats app not installed on your device",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//
//        }

        if (sendexcreq){
            sendexcreq=false
            sendNotification(PushNotification(NotificationData(title,message),seller?.fcmToken!!))
            LaunchedEffect(key1 = true){
                scope.launch(Dispatchers.Main) {
                    viewModel2.insertNotification(
                        NotificationContent.NotificationItem
                            (title = title,message=exccovermessage,time = System.currentTimeMillis(),
                            receiverID = seller.userId,senderID = buyer?.userId, read = "false",ad = item1)).collect {
                        when (it) {
                            is ResultState.Success -> {
                                context.showMsg(
                                    msg = ""
                                )
                                showexchangesheet=false
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
                            receiverId = item.item?.sellerId,message=exccovermessage,category = "Exchange",read = "false", ad = item, time = System.currentTimeMillis())).collect {
                        when (it) {
                            is ResultState.Success -> {
                                context.showMsg(
                                    msg = "Exchange Request Sent"
                                )
                                showexchangesheet=false
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
        if (showdeletesheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showdeletesheet = false
                },
                sheetState = sheetState
            ){

                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Want to delete the item?",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "*this is an irreversible process",
                            color = Color.Gray,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(Modifier
                            .fillMaxWidth(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                scope.launch(Dispatchers.Main) {
                                    viewModel4
                                        .delete(item.key!!)
                                        .collect {
                                            when (it) {
                                                is ResultState.Success -> {
                                                    context.showMsg(
                                                        msg = "Ad deleted"
                                                    )
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            MainActivity::class.java
                                                        )
                                                    intent.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    context.startActivity(intent)
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
                            .background(main), contentAlignment = Alignment.Center) {
                            Row {
                                Box(
                                    modifier = Modifier.fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showmarkassoldsheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showmarkassoldsheet = false
                },
                sheetState = sheetState
            ){

                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Is the item sold?",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Mark the item as sold.",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "*this is an irreversible process",
                            color = Color.Gray,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(Modifier
                            .fillMaxWidth(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                markassold = true
                            }
                            .background(main), contentAlignment = Alignment.Center) {
                            Row {
                                Box(
                                    modifier = Modifier.fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mark as sold",
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showexchangesheet){

            ModalBottomSheet(
                onDismissRequest = {
                    showexchangesheet = false
                },
                sheetState = sheetState
            ){
                if (isUserinDB) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Send a cover message!",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Offers with great cover messages are more likely to get accepted. \n tell about the items you want to exchange",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            OutlinedTextField(
                                value = exccovermessage,
                                onValueChange = {
                                    if (it.length <= 300) {
                                        exccovermessage = it
                                    }
                                },
                                label = {
                                    Text(text = "Cover Message")
                                },
                                shape = RoundedCornerShape(15.dp),
                                maxLines = 6,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedLabelColor = Color.Black,
                                    focusedLeadingIconColor = Color.Black,
                                    focusedBorderColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    cursorColor = Color.Black,
                                    unfocusedLabelColor = Color.Gray,
                                    unfocusedBorderColor = Color.Gray,
                                    unfocusedLeadingIconColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 20.dp), contentAlignment = Alignment.TopEnd
                            ) {
                                Text(
                                    text = "${exccovermessage.length}/300 ",
                                    color = if (exccovermessage.length > 300) Color.Red else Color.Gray,
                                    modifier = Modifier.padding(start = 16.dp),
                                    fontWeight = FontWeight.Thin,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Box(Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    sendexcreq = true
                                }
                                .background(main), contentAlignment = Alignment.Center) {
                                Row {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Send the Offer!",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            if (item.item?.whatsapp == "true") {
                                Spacer(modifier = Modifier.height(15.dp))
                                Box(Modifier
                                    .fillMaxWidth(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onClick(exccovermessage)
                                    }
                                    .background(main), contentAlignment = Alignment.Center) {
                                    Row(
                                        Modifier.fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = "Send the Offer with",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Image(
                                            painter = painterResource(id = R.drawable.whatsapp),
                                            modifier = Modifier.size(30.dp),
                                            contentDescription = ""
                                        )

                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Complete Your Profile first",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                if (offerscreen && !showcover &&isUserinDB) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Buying this product? 💵",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = "Make an offer now !",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(modifier = Modifier
                                    .size(60.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        if (price!! > 10) {
                                            price = price!! - 10
                                        }
                                    }
                                    .background(Color.LightGray),
                                    contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "-",
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .height(60.dp)
                                        .clip(
                                            RoundedCornerShape(10.dp)
                                        )

                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(modifier = Modifier.padding(start = 25.dp, end = 25.dp)) {
                                        Text(
                                            text = "₹ ${price}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Light
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(modifier = Modifier
                                    .size(60.dp)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                                    .background(Color.LightGray)
                                    .clickable {
                                        price = price!! + 10

                                    }, contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+",
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Box(Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    showcover = true
                                    offerscreen = false
                                }
                                .background(main), contentAlignment = Alignment.Center) {
                                Row {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Finalize the price",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                var covermessage by remember {
                    mutableStateOf("Hi, I am willing to purchase your product : ${item.item?.title}, I am making an offer of amount ${price}. \n\nLooking for a positive response from your side!")
                }
                LaunchedEffect(price) {
                    covermessage = "Hi, I am willing to purchase your product : ${item.item?.title}, I am making an offer of amount ${price}. \n\nLooking for a positive response from your side!"
                }
                var burreqtitle="Buy Request Received"
                if (sendbuyreq){
                    sendbuyreq=false
                    sendNotification(PushNotification(NotificationData(burreqtitle,if (covermessage.length>=99) "New Buy request from ${buyer?.name}\n${covermessage.substring(0,99)}" else "New Buy request from ${buyer?.name} \n$covermessage"),seller?.fcmToken!!))
                    LaunchedEffect(key1 = true){
                        scope.launch(Dispatchers.Main) {
                            viewModel2.insertNotification(
                                NotificationContent.NotificationItem
                                    (title = burreqtitle,message=covermessage,time = System.currentTimeMillis(),
                                    receiverID = seller.userId,senderID = buyer?.userId, read = "false",ad = item1)).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = ""
                                        )
                                        showBottomSheet=false
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
                                    receiverId = item.item?.sellerId,message=covermessage,category = "Buy",read = "false", ad = item, time = System.currentTimeMillis())).collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = "Buy Request Sent"
                                        )
                                        showBottomSheet=false

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
                if (showcover && !offerscreen && isUserinDB){
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Send a cover message!",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Offers with great cover messages are more likely to get accepted.",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            OutlinedTextField(
                                value = covermessage,
                                onValueChange = {
                                    if (it.length <= 300) {
                                        covermessage = it
                                    }
                                },
                                label = {
                                    Text(text = "Cover Message")
                                },
                                shape = RoundedCornerShape(15.dp),
                                maxLines = 6,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp), contentAlignment = Alignment.TopEnd){
                                Text(
                                    text = "${covermessage.length}/300 ",
                                    color = if (covermessage.length > 300) Color.Red else Color.Gray,
                                    modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Thin, fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Box(Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    sendbuyreq = true
                                }
                                .background(main), contentAlignment = Alignment.Center) {
                                Row {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Send the Offer!",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            if (item.item?.whatsapp=="true") {
                                Spacer(modifier = Modifier.height(15.dp))
                                Box(Modifier
                                    .fillMaxWidth(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {

                                    }
                                    .background(main), contentAlignment = Alignment.Center) {
                                    Row (Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){

                                            Text(
                                                text = "Send the Offer with",
                                                color = Color.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Image(painter = painterResource(id = R.drawable.whatsapp), modifier = Modifier.size(30.dp), contentDescription = "")

                                    }
                                }
                            }
                        }
                    }

                }
                if (!isUserinDB){

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Complete Your Profile first",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp
                        )
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
        .padding(5.dp)
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
private fun appInstalledOrNot(url: String,context: Context): Boolean {
    val packageManager: PackageManager = context.packageManager
    val app_installed: Boolean
    app_installed = try {
        packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return app_installed
}