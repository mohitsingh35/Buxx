package com.ncs.tradezy

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun adHost(item:EachAdResponse,viewModel: ProfileActivityViewModel= hiltViewModel()){
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

    var sendExchangeNotification by remember {
        mutableStateOf(false)
    }
    val  title="New Exchange Request"
    val message="Exchange Request from ${buyer?.name}. Tap here to know more."
    if (sendExchangeNotification){
        sendExchangeNotification=false
        sendNotification(PushNotification(NotificationData(title,message),seller?.fcmToken!!))
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(primary)
        .padding(16.dp)){
        LazyColumn {
            items(1){
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    items(item.item?.images?.size!!) { index ->
                        AsyncImage(model = item.item.images[index], contentDescription =  " ",modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .padding(end = 5.dp), contentScale = ContentScale.Crop)
                    }
                }
                Text(text = item.item?.title!!, color = betterWhite, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(25.dp))
                Text(text = item.item?.desc!!, color = betterWhite, fontSize = 18.sp)
                AsyncImage(model = seller?.profileDPurl, contentDescription = "", modifier = Modifier.size(40.dp))
                Text(text = "Posted by ${seller?.name}", color = betterWhite)
                Text(text = "Posted on ${convertLongToTimeString(item.item.time!!)}", color = betterWhite)
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(50.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Buy")
                    }

                    if (item.item.exchangeable =="true"){
                        Button(onClick = {
                            sendExchangeNotification=true
                        }) {
                            Text(text = "Exchange")
                        }
                    }
                }
            }

        }
    }

}

private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful) {
            Log.d(TAG, "Response: ${Gson().toJson(response)}")
        } else {
            Log.e(TAG, response.errorBody().toString())
        }
    } catch(e: Exception) {
        Log.e(TAG, e.toString())
    }
}