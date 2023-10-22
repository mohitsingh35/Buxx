package com.ncs.tradezy

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.msgreceive
import com.ncs.tradezy.ui.theme.msgsent
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun notificationsScreen(viewModel: NotificationViewModel= hiltViewModel(), viewModel2: ProfileActivityViewModel= hiltViewModel(), viewModel3:PromoNotificationViewModel= hiltViewModel(), navController: NavController){
    val res=viewModel.res.value   //noti
    val res2=viewModel2.res.value //user
    val res3=viewModel3.res.value //promonoti
    var allselected by remember {
        mutableStateOf(true)
    }
    var reqselected by remember {
        mutableStateOf(false)
    }
    var promotion by remember {
        mutableStateOf(false)
    }
    var promoNoti=ArrayList<NotificationContent>()
    for (i in 0 until res3.item.size){
        promoNoti.add(res3.item[i])
    }
    val currentuser=FirebaseAuth.getInstance().currentUser?.uid
    var filtereNotiList=ArrayList<NotificationContent>()
    var allNotiList=ArrayList<NotificationContent>()

    for (i in 0 until res.item.size){
        if (res.item[i].item?.receiverID==currentuser){
            filtereNotiList.add(res.item[i])
        }
    }
//    var buyerList=ArrayList<String>()
//    for (i in 0 until filtereNotiList.size){
//        buyerList.add(filtereNotiList[i].item?.senderID!!)
//    }

    var noticount = 0
    for (i in 0 until filtereNotiList.size) {
        if (filtereNotiList[i].item?.read == "false") {
            noticount++
        }
    }
    val context= LocalContext.current
    if (promoNoti.isNotEmpty() || filtereNotiList.isNotEmpty()) {
        allNotiList.addAll(promoNoti)
        allNotiList.addAll(filtereNotiList)
    }
    var buyerList=ArrayList<String>()

    var myallnoti: ArrayList<NotificationContent>
    val myallnoti1= allNotiList.sortedByDescending { it.item?.time }
    myallnoti= ArrayList(myallnoti1)
    for (i in 0 until myallnoti.size){
        buyerList.add(myallnoti[i].item?.senderID!!)
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp, start = 10.dp, end = 10.dp)
            .background(background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)) {
            Box(modifier = Modifier
                .padding(start = 10.dp)
                .clip(CircleShape)
                .clickable {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
            }
            Row(
                modifier = Modifier
                    .padding(start = 10.dp )
            ) {
                Text(
                    text = "Notifications ",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
//                if (noticount>1){
//                    Box(modifier = Modifier.height(25.dp), contentAlignment = Alignment.Center){
//                        Text(
//                            text = "($noticount new)",
//                            fontSize = 16.sp,
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier
                .height(40.dp)
                .clip(
                    RoundedCornerShape(5.dp)
                )
                .clickable {
                    allselected = true
                    reqselected = false
                    promotion = false
                }
                .weight(1f)
                .background(if (allselected) main else Color.LightGray)
                .padding(5.dp)
                , contentAlignment = Alignment.Center){
                Text(
                    text = "All",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier
                .height(40.dp)
                .clip(
                    RoundedCornerShape(5.dp)
                )
                .weight(1f)
                .clickable {
                    allselected = false
                    reqselected = true
                    promotion = false
                }
                .background(if (reqselected) main else Color.LightGray)
                .padding(5.dp)
                , contentAlignment = Alignment.Center){
                Text(
                    text = "Requests",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Box(modifier = Modifier
                .height(40.dp)
                .clip(
                    RoundedCornerShape(5.dp)
                )
                .weight(1f)
                .clickable {
                    allselected = false
                    reqselected = false
                    promotion = true
                }

                .background(if (promotion) main else Color.LightGray)
                .padding(5.dp)
                , contentAlignment = Alignment.Center){
                Text(
                    text = "Promotion",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Light
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box {
            if (allselected){
                if (res.isLoading){
                    mainLoading()
                }
                else {
                    if (myallnoti.isEmpty()){
                        emptyscreen()
                    }
                    else {
                        LazyColumn() {
                            items(1) {
                                for (i in 0 until myallnoti.size) {
                                    if (myallnoti[i].item?.sendername!=""){
                                        promtioneachNoti(item = myallnoti[i])
                                    }
                                    if (myallnoti[i].item?.sendername=="" || myallnoti[i].item?.sendername==null) {
                                        eachNotificationbox(
                                            item = myallnoti[i],
                                            buyer = buyerList[i]
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (reqselected){
                if (res.isLoading){
                    mainLoading()
                }
                else {
                    if (filtereNotiList.isEmpty()){
                        emptyscreen()
                    }
                    else {
                        LazyColumn() {
                            items(1) {
                                for (i in 0 until filtereNotiList.size) {
                                    eachNotificationbox(
                                        item = filtereNotiList[i],
                                        buyer = buyerList[i]
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (promotion){
                if (res.isLoading){
                    mainLoading()
                }
                else {
                    if (promoNoti.isEmpty()){
                        emptyscreen()
                    }
                    else {
                        LazyColumn() {
                            items(1) {
                                for (i in 0 until promoNoti.size) {
                                    promtioneachNoti(item = promoNoti[i])
                                }
                            }
                        }
                    }
                }
            }


        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun promtioneachNoti(item:NotificationContent,viewModel: PromoNotificationViewModel= hiltViewModel()){


    var showDialog by remember {
        mutableStateOf(false)
    }
    var read by remember {
        mutableStateOf(false)
    }
    if (item.item?.msgread?.containsKey(FirebaseAuth.getInstance().currentUser?.uid!!) == true){
        if (item.item?.msgread?.getValue(FirebaseAuth.getInstance().currentUser?.uid!!)==null){
            read=false
        }
    }
    if (item.item?.msgread?.containsKey(FirebaseAuth.getInstance().currentUser?.uid!!) == false){
        read=false
    }
    if (item.item?.msgread?.containsKey(FirebaseAuth.getInstance().currentUser?.uid!!) == true){
        if (item.item?.msgread?.getValue(FirebaseAuth.getInstance().currentUser?.uid!!)=="true"){
            read=true
        }
    }
    val databaseReference = FirebaseDatabase.getInstance().reference.child("data").child("promotional")

    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    if (showDialog){
        AlertDialog(onDismissRequest = { showDialog=false }, confirmButton = {}, text = {
            Column {
                Row {
                    AsyncImage(model = item.item?.senderurl, contentDescription = "",
                        Modifier
                            .clip(CircleShape)
                            .size(40.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column (){
                        Text(text = item.item?.title!!, color = Color.Black, fontWeight = FontWeight.Medium , fontSize = 18.sp, )
                        Text(text = "~from ${item.item.sendername}", color = Color.Black, fontWeight =FontWeight.Medium , fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = item.item?.message!!, color = Color.Black, fontWeight =FontWeight.Medium , fontSize = 14.sp)


                }
            }
        }
        )
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)
        .clip(RoundedCornerShape(20.dp))
        .height(140.dp)
        .clickable {
            showDialog = true
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            val msgRead = item.item?.msgread?.toMutableMap()

            currentUserUid?.let { uid ->
                msgRead?.set(uid, "true")
            }

            scope.launch(Dispatchers.Main) {
                viewModel
                    .update(
                        NotificationContent(
                            item = NotificationContent.NotificationItem(
                                msgread = msgRead
                            ),
                            key = item.key
                        )
                    )
                    .collect {
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
        .background(if (!read) main else background)){
        Row (modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Column(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!read) msgsent else background)
                    .padding(10.dp)) {
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    AsyncImage(model = item.item?.senderurl, contentDescription = "",
                        Modifier
                            .clip(CircleShape)
                            .size(37.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = item.item?.title!!, color = Color.Black, fontWeight = if (!read) FontWeight.Bold else FontWeight.Medium, fontSize = 20.sp, maxLines = 1)
                        Text(text = item.item?.message!!, color = Color.Gray, fontWeight = if (!read) FontWeight.Bold else FontWeight.Medium,fontSize = 10.sp, maxLines = 1)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        showDialog = true
                        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                        val msgRead = HashMap<String, String>()
                        currentUserUid?.let { uid ->
                            msgRead[uid] = "true"
                        }
                        scope.launch(Dispatchers.Main) {
                            viewModel
                                .update(
                                    NotificationContent(
                                        item = NotificationContent.NotificationItem(
                                            msgread = msgRead
                                        ),
                                        key = item.key
                                    )
                                )
                                .collect {
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
                    .background(if (!read) main else betterWhite), contentAlignment = Alignment.Center) {
                    Row {
                        Text(text = "View", color = Color.Black, fontWeight = FontWeight.Light, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, start = 10.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Text(text = convertLongToDate(item.item?.time!!), fontSize = 12.sp, color = if (!read) Color.Gray else Color.LightGray, fontWeight = if (!read) FontWeight.Bold else FontWeight.Medium)
                    Text(text = convertLongToTime(item.item?.time!!), fontSize = 12.sp, color = if (!read) Color.Gray else Color.LightGray, fontWeight = if (!read) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun eachNotificationbox(item:NotificationContent,buyer:String,viewModel2: ProfileActivityViewModel= hiltViewModel(),viewModel: NotificationViewModel= hiltViewModel()){
    val res=viewModel2.res.value
    var user:RealTimeUserResponse.RealTimeUsers?=null
    for (i in 0 until res.item.size){
        if (res.item[i].item?.userId==buyer){
            user=res.item[i].item!!
        }
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    var markasRead by remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    if (showDialog){
        AlertDialog(onDismissRequest = { showDialog=false }, confirmButton = {}, text = {
            Column {
                Row {
                    AsyncImage(model = user?.profileDPurl, contentDescription = "",
                        Modifier
                            .clip(CircleShape)
                            .size(40.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column (){
                        Text(text = item.item?.title!!, color = Color.Black, fontWeight = FontWeight.Medium , fontSize = 18.sp)
                        Text(text = "~from ${user?.name!!}", color = Color.Black, fontWeight =FontWeight.Medium , fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    eachAd(item = item.item?.ad!!)
                }
            }
        }
        )
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)
        .clip(RoundedCornerShape(20.dp))
        .height(140.dp)
        .clickable {
            showDialog = true
            markasRead = true
            scope.launch(Dispatchers.Main) {
                viewModel
                    .update(
                        NotificationContent(
                            item = NotificationContent.NotificationItem(
                                read = "true"
                            ),
                            key = item.key
                        )
                    )
                    .collect {
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
        .background(if (item.item?.read == "false") main else background)){
        Row (modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Column(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (item.item?.read == "false") msgsent else background)
                    .padding(10.dp)) {
                Spacer(modifier = Modifier.height(5.dp))
                Row {
                    AsyncImage(model = user?.profileDPurl, contentDescription = "",
                        Modifier
                            .clip(CircleShape)
                            .size(37.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = item.item?.title!!, color = Color.Black, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium, fontSize = 20.sp, maxLines = 1)
                        Text(text = item.item?.message!!, color = Color.Gray, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium,fontSize = 10.sp, maxLines = 1)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        showDialog = true
                        markasRead = true
                        scope.launch(Dispatchers.Main) {
                            viewModel
                                .update(
                                    NotificationContent(
                                        item = NotificationContent.NotificationItem(
                                            read = "true"
                                        ),
                                        key = item.key
                                    )
                                )
                                .collect {
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
                    .background(if (item.item?.read == "false") main else betterWhite), contentAlignment = Alignment.Center) {
                    Row {
                        Text(text = "View", color = Color.Black, fontWeight = FontWeight.Light, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, start = 10.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Text(text = convertLongToDate(item.item?.time!!), fontSize = 12.sp, color = if (item.item?.read == "false") Color.Gray else Color.LightGray, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium)
                    Text(text = convertLongToTime(item.item?.time!!), fontSize = 12.sp, color = if (item.item?.read == "false") Color.Gray else Color.LightGray, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium)
                }
            }
        }

    }
}