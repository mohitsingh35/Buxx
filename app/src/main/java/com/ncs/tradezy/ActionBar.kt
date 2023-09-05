package com.ncs.tradezy

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.secondary


//23-08-2023 8:00 P.M.

@Composable
fun setActionBar(screenName:String, image: Int,navController: NavController,viewModel: NotificationViewModel= hiltViewModel()){
    val res=viewModel.res.value
    var noticount=0
    val currentuser= FirebaseAuth.getInstance().currentUser?.uid
    var filtereNotiList=ArrayList<NotificationContent>()
    for (i in 0 until res.item.size){
        if (res.item[i].item?.receiverID==currentuser){
            filtereNotiList.add(res.item[i])
        }
    }
    for (i in 0 until filtereNotiList.size){
        if (filtereNotiList[i].item?.read=="false"){
            noticount++
        }
    }
    val context= LocalContext.current
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(primary)

        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(Modifier.padding(15.dp)){
                    Text(
                        text = screenName,
                        color = accent,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )

                }
                Row {
                    Box(Modifier.fillMaxHeight(),contentAlignment = Alignment.Center){
                        Row {
                            Text(text = noticount.toString(),
                                Modifier
                                    .clip(CircleShape)
                                    .size(25.dp)
                                    .background(
                                        Color.Red
                                    ), fontSize = 20.sp, color = betterWhite, textAlign = TextAlign.Center)
                            Icon(imageVector = Icons.Filled.Notifications, contentDescription = "", tint = betterWhite, modifier = Modifier
                                .clickable {
                                    navController.navigate("notificationScreen")
                                }
                                .size(25.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(imageVector = Icons.Filled.Email, contentDescription = "", tint = betterWhite, modifier = Modifier
                                .clickable {
                                    context.startActivity(Intent(context, ChatActivity::class.java))

                                }
                                .size(25.dp))

                        }
                        
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                    ) {
                        Image(painterResource(id = image), contentDescription = "",Modifier.clickable {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        })
                    }
                }

            }
        }

    }
}