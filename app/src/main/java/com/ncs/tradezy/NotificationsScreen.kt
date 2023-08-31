package com.ncs.tradezy

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun notificationsScreen(viewModel: NotificationViewModel= hiltViewModel(),viewModel2: ProfileActivityViewModel= hiltViewModel(),navController: NavController){
    val res=viewModel.res.value   //noti
    val res2=viewModel2.res.value //user
    val currentuser=FirebaseAuth.getInstance().currentUser?.uid
    var filtereNotiList=ArrayList<NotificationContent>()
    for (i in 0 until res.item.size){
        if (res.item[i].item?.receiverID==currentuser){
            filtereNotiList.add(res.item[i])
        }
    }
    var buyerList=ArrayList<String>()
    for (i in 0 until filtereNotiList.size){
        buyerList.add(filtereNotiList[i].item?.senderID!!)
    }



    Box(modifier = Modifier
        .fillMaxSize()
        .background(primary)
        .padding(start = 16.dp, end = 16.dp, top = 30.dp)){
        LazyColumn(){
            items(1){
                for (i in 0 until filtereNotiList.size){
                    eachNotificationbox(item = filtereNotiList[i], buyer = buyerList[i] )

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
                Text(text = item.item?.title!!)
                Text(text = item.item?.message!!)
                Row {
                    Text(text = user?.name!!)
                    AsyncImage(model = user?.profileDPurl, contentDescription = "",
                        Modifier
                            .clip(
                                CircleShape
                            )
                            .size(25.dp))
                }
            }
        }
        )
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(1.dp)
        .height(100.dp)
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
        .background(if (item.item?.read == "false") Color.Gray else primary)){
        Row (modifier = Modifier
            .fillMaxSize()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Column {
                Text(text = item.item?.title!!, color = betterWhite, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium, fontSize = 20.sp)
                Text(text = item.item?.message!!, color = betterWhite, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium,fontSize = 10.sp)
                Text(text = convertLongToTimeString(item.item?.time!!), color = betterWhite, fontWeight = if (item.item?.read == "false") FontWeight.Bold else FontWeight.Medium)
            }
            Box(modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (item.item?.read == "false") Color.Green else Color.Gray))
        }

    }
}