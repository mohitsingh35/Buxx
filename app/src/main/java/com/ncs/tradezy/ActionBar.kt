package com.ncs.tradezy

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.secondary
import kotlinx.coroutines.delay


@Composable
fun setActionBar(screenName:String, image: Int,navController: NavController,viewModel: NotificationViewModel= hiltViewModel(),viewModel2: ChatViewModel= hiltViewModel(),userViewModel:ProfileActivityViewModel= hiltViewModel()) {
    val res = viewModel.res.value
    val user=userViewModel.res.value
    var messagecount=0
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid
    var current = ArrayList<RealTimeUserResponse>()
    var notinDB by remember {
        mutableStateOf(false)
    }
    if (user.item.isNotEmpty()) {
        for (i in 0 until user.item.size) {
            if (user.item[i].item?.userId == currentuser) {
                current.add(user.item[i])
            }
        }
    }

    val context = LocalContext.current

    val res2=viewModel2.res.value
    if (res2.item.isNotEmpty()) {


        val userMessages = mutableListOf<MessageResponse>()
        val latestMessagesMap = HashMap<String, MessageResponse>()

        for (i in 0 until res2.item.size) {
            val message = res2.item[i]
            val senderId = message.item?.senderId
            val receiverId = message.item?.receiverId
            val otherUserId = if (senderId == currentuser) receiverId else senderId

            if (senderId == currentuser || receiverId == currentuser) {
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
        for (i in 0 until userMessages.size) {
            if (userMessages[i].item?.receiverId == currentuser && userMessages[i].item?.read=="false") {
                messagecount++
            }
        }
    }



    Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .background(background)

            ) {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(Modifier.padding(15.dp)) {
                        if (current.isNotEmpty()){
                            Text(
                                text =if (user.item.isEmpty()) "Welcome" else "Hi, ${current[0].item?.name?.substringBefore(" ")}" ,
                                color = Color.Black,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        else{
                            Text(
                                text ="Welcome" ,
                                color = Color.Black,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Row {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .padding(end = 25.dp, top = 10.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            ChatActivity::class.java
                                        )
                                    )
                                }){
                            Box(Modifier.padding(top = 5.dp)) {
                                Icon(painter = painterResource(R.drawable.msg_icon),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(30.dp))
                            }
                            if (messagecount>0){
                                Box(Modifier.padding(start = 21.dp, bottom = 15.dp)){
                                    Text(
                                        text = messagecount.toString(),
                                        Modifier
                                            .clip(CircleShape)
                                            .size(18.dp)
                                            .background(
                                                Color.Red
                                            ),
                                        fontSize = 12.sp,
                                        color = betterWhite,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                    }

                }
            }

        }
    }
