package com.ncs.tradezy

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.secondary


@Composable
fun bottomBar(
    items:List<BottomBarContent>,
    navController: NavController,
    onItemClick: (BottomBarContent) -> Unit,
    googleAuthUIClient: GoogleAuthUIClient,
    noticount: Int
){
    val backStackEntry=navController.currentBackStackEntryAsState()
    Column (Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center){
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color.LightGray)) {
        }
        Row (
            modifier = Modifier
                .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            items.forEach{item ->
                val state=item.route==backStackEntry.value?.destination?.route
                BottomBarItem(item = item, state = state, onItemClick = {onItemClick(item)}, googleAuthUIClient = googleAuthUIClient, noticount = noticount)
            }

        }
    }
}
@Composable
fun BottomBarItem(
    item: BottomBarContent,
    state:Boolean=false,
    selectedColor: Color = accent,
    onItemClick:()-> Unit,googleAuthUIClient: GoogleAuthUIClient,
    noticount:Int
){
    val context= LocalContext.current
    Row() {
        Box(modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (state) main else Color.Transparent)
            .clickable { onItemClick() }, contentAlignment = Alignment.Center){
            if (item.route=="profile"){
                AsyncImage(model = googleAuthUIClient.getSignedInUser()?.profilePictureUrl, contentDescription = "",modifier = Modifier
                    .size(35.dp).clip(CircleShape).clickable { context.startActivity(Intent(context,ProfileActivity::class.java)) }
                )

            }

            else if (item.route=="notificationScreen"){
                Box(Modifier.fillMaxHeight()){
                    Box(Modifier.padding(top = 5.dp)) {
                        Icon(painter = painterResource(item.iconId),
                            contentDescription = "",
                            tint = if (state) Color.Black else Color.Gray,
                            modifier = Modifier
                                .size(30.dp))
                    }
                    if (noticount>0){
                        Box(Modifier.padding(start = 15.dp, bottom = 5.dp)){
                            Text(
                                text = noticount.toString(),
                                Modifier
                                    .clip(CircleShape)
                                    .size(20.dp)
                                    .background(
                                        Color.Red
                                    ),
                                fontSize = 15.sp,
                                color = betterWhite,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            else {
                Icon(
                    painter = painterResource(item.iconId),
                    contentDescription = "item",
                    modifier = Modifier
                        .size(33.dp),
                    tint = if (state) Color.Black else Color.Gray,
                )

            }
        }
    }

}