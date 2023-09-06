package com.ncs.tradezy

import android.content.ContentValues
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.ncs.tradezy.repository.RealTimeUserResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatHostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val name = intent.getStringExtra("name")
            val id = intent.getStringExtra("id")
            val token = intent.getStringExtra("token")
            chatHost(name =  name!! , id = id!!, fcmtoken = token!! )
        }
    }
}

@Composable
fun chatHost(name:String,id:String,fcmtoken:String) {
    val viewModel: ChatViewModel = hiltViewModel()
    val viewModel2: ProfileActivityViewModel = hiltViewModel()
    val scope= rememberCoroutineScope()
    val context = LocalContext.current


    var message = remember {
        mutableStateOf("")
    }
    val res = viewModel.res.value
    val res2 = viewModel2.res.value

    val senderid = FirebaseAuth.getInstance().currentUser?.uid
    val chatList= ArrayList<MessageResponse>()
    val currentUserData= ArrayList<RealTimeUserResponse>()

    for (i in 0 until res.item.size){
        if (res.item[i].item!!.receiverId.equals(id)&&res.item[i].item!!.senderId.equals(senderid)||
            res.item[i].item!!.receiverId.equals(senderid)&&res.item[i].item!!.senderId.equals(id)){
            chatList.add(res.item[i])
        }

    }
    for (i in 0 until res2.item.size){
        if (res2.item[i].item?.userId==senderid){
            currentUserData.add(res2.item[i])
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                .background(Color.LightGray), contentAlignment = Alignment.Center
        ) {
            Text(text = name, fontSize = 20.sp)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .background(Color.Green)
        ) {
            if (res.item.isNotEmpty()) {
                LazyColumn {
                    items(chatList) { chatItem ->
                        if (chatItem.item!!.senderId == senderid) {
                            messageSender(itemState = chatItem)

                        } else {
                            MessageReceiver(itemState = chatItem)
                        }
                    }
                }
            }

            if (res.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            if (res.error.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = res.error)
                }
            }

        }
        Box(
            modifier = Modifier
                .fillMaxWidth()

                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    placeholder = {
                        Text(
                            text = "Enter Message"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        sendNotification(PushNotification(NotificationData(currentUserData[0].item?.name!!,message.value),fcmtoken))
                        scope.launch(Dispatchers.Main) {
                            if (message.value.isNotEmpty()) {
                                viewModel
                                    .insertMessage(
                                        MessageResponse.MessageItems
                                            (
                                            senderId = senderid,
                                            receiverId = id,
                                            message = message.value,
                                            category = "Exchange",
                                            read = "false",
                                            System.currentTimeMillis()
                                        )
                                    )
                                    .collect {
                                        when (it) {
                                            is ResultState.Success -> {
                                                context.showMsg(
                                                    msg = it.data
                                                )
                                                message.value = ""
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
                            } else {
                                context.showMsg("Message cannot be Empty")
                            }

                        }
                    }, contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "")

                }
            }
        }
    }


}
@Composable
fun messageSender(itemState: MessageResponse) {
    val context= LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        if (itemState.item?.ad!=null){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(context, AdHostActivity::class.java)
                        intent.putExtra("clickedItem", itemState.item.ad)
                        context.startActivity(intent)
                    }
                    .padding(20.dp)
            ) {
                AsyncImage(model = itemState.item.ad.item?.images?.get(0), contentDescription = "")
                Text(text = itemState.item.ad.item?.title!!)
                Text(text = itemState.item.ad.item?.desc!!, maxLines = 2)
                Text(text = itemState.item.message!!)
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = itemState.item?.message!!)
            }
        }

    }
}
@Composable
fun MessageReceiver(itemState: MessageResponse,viewModel: ChatViewModel= hiltViewModel()) {
    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    LaunchedEffect(key1=true ){
        scope.launch(Dispatchers.Main) {
            viewModel.update(
                MessageResponse(item = MessageResponse.MessageItems(read = "true"),key = itemState.key)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
    ) {
        if (itemState.item?.ad!=null){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(context, AdHostActivity::class.java)
                        intent.putExtra("clickedItem", itemState.item.ad)
                        context.startActivity(intent)
                    }
                    .padding(20.dp)
            ) {
                AsyncImage(model = itemState.item.ad.item?.images?.get(0), contentDescription = "")
                Text(text = itemState.item.ad.item?.title!!)
                Text(text = itemState.item.ad.item?.desc!!, maxLines = 2)
                Text(text = itemState.item.message!!)
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = itemState.item?.message!!)
            }
        }
    }

}
private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful) {
            Log.d(ContentValues.TAG, "Response")
        } else {
            Log.e(ContentValues.TAG, response.errorBody().toString())
        }
    } catch(e: Exception) {
        Log.e(ContentValues.TAG, e.toString())
    }
}
