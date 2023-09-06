package com.ncs.tradezy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            showList()
        }
    }
}

@Composable
fun showList(viewModel: ChatViewModel= hiltViewModel()){
    val currentUser=FirebaseAuth.getInstance().currentUser?.uid
    val res=viewModel.res.value
    if (res.item.isNotEmpty()){


        val userMessages = mutableListOf<MessageResponse>()
        val latestMessagesMap = HashMap<String, MessageResponse>()

        for (i in 0 until res.item.size) {
            val message = res.item[i]
            val senderId = message.item?.senderId
            val receiverId = message.item?.receiverId
            val otherUserId = if (senderId == currentUser) receiverId else senderId

            if (senderId == currentUser || receiverId == currentUser) {
                val existingLatestMessage = latestMessagesMap[otherUserId]

                if (existingLatestMessage == null) {
                    latestMessagesMap[otherUserId!!] = message
                } else {
                    val existingTime = existingLatestMessage.item?.time
                    val currentTime = message.item?.time

                    if (currentTime != null && (existingTime == null || currentTime > existingTime)) {
                        latestMessagesMap[otherUserId!!] = message
                    }
                }
            }
        }

        userMessages.addAll(latestMessagesMap.values)
        userMessages.sortByDescending { it.item?.time }
        Box(modifier = Modifier
            .background(betterWhite)
            .fillMaxSize()
            .padding(10.dp)){
            LazyColumn(){
                items(1){

                    for (i in 0 until userMessages.size){

                            if (userMessages[i].item?.senderId==currentUser){
                                eachRow(message = userMessages[i].item?.message!!, isRead =true, otherId = userMessages[i].item?.receiverId!!,)
                            }
                            else{
                                eachRow(message = userMessages[i].item?.message!!, isRead = userMessages[i].item?.read.toBoolean(), otherId = userMessages[i].item?.senderId!!,)
                            }
                        }
                    }

            }
        }
    }

}
@Composable
fun eachRow(message:String,isRead:Boolean,viewModel2: ProfileActivityViewModel= hiltViewModel(),otherId:String,){
    var sender=ArrayList<RealTimeUserResponse>()
    val res=viewModel2.res.value
    val context= LocalContext.current
    for (i in 0 until res.item.size){
        if (res.item[i].item?.userId==otherId){
            sender.add(res.item[i])
        }
    }
    if (sender.isNotEmpty()){
        Row (
            Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(context, ChatHostActivity::class.java)
                    intent.putExtra("name", sender[0].item?.name)
                    intent.putExtra("id",sender[0].item?.userId )
                    intent.putExtra("token",sender[0].item?.fcmToken )
                    context.startActivity(intent)
                }
                .background(Color.LightGray)
                .height(80.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Row(modifier = Modifier.padding(10.dp)){
                AsyncImage(model = sender[0].item?.profileDPurl, contentDescription = "", modifier = Modifier
                    .size(40.dp)
                    .clip(
                        CircleShape
                    ) )
                Spacer(modifier = Modifier.width(20.dp))
                Column(Modifier.fillMaxWidth(0.7f)) {
                    Text(text = sender[0].item?.name!!, fontWeight = if (!isRead) FontWeight.Bold else FontWeight.Medium)
                    Text(text = message, fontWeight = if (!isRead) FontWeight.Bold else FontWeight.Medium, fontSize = 15.sp, maxLines = 1)
                }
            }
            Box (Modifier.padding(20.dp)){
                Box(modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (!isRead) main else betterWhite))
            }

        }
    }

}



