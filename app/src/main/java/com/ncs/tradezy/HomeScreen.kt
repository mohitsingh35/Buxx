package com.ncs.tradezy

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.greenbg
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.secondary
import java.io.Serializable

@Composable
fun itemHolder(items:List<EachAdResponse>, intitalSelectedItem: EachAdResponse?=null){
    var clicked by remember {
        mutableStateOf(intitalSelectedItem)
    }
    val context= LocalContext.current
    Column(Modifier.width(150.dp)) {
        items.forEachIndexed { index, bannerContent ->
            eachItem(item = bannerContent, index = index, onItemClick = {
                clicked=it
            })
        }
    }

}

@Composable
fun eachItem(
    item: EachAdResponse,
    index:Int,
    onItemClick:(EachAdResponse)-> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .height(290.dp)
            .padding(3.dp)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(0.5.dp, Color.LightGray),
        elevation = 5.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
            if (item.item?.price == 0) {
                Box(
                    modifier = Modifier
                        .height(35.dp)
                        .fillMaxWidth()
                        .background(
                            main
                        ), contentAlignment = Alignment.Center
                ) {
                    Row(Modifier.padding(start = 5.dp, end = 5.dp)) {
                        Text(
                            text = "₹ Free",
                            color = Color.DarkGray,
                            fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

            }
            if (item.item?.price != 0) {
                Box(
                    modifier = Modifier
                        .height(35.dp)
                        .fillMaxWidth()
                        .background(
                            main
                        ), contentAlignment = Alignment.Center
                ) {
                    Row(Modifier.padding(start = 5.dp, end = 5.dp)) {
                        Text(
                            text = "₹ ${item.item?.price}",
                            color = Color.DarkGray,
                            fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

            }
        }
        Column {
            Box(modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    onItemClick(item)
                    val intent = Intent(context, AdHostActivity::class.java)
                    intent.putExtra("clickedItem", item)
                    context.startActivity(intent)
                }
                .height(300.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        LazyRow(
                            Modifier
                                .height(200.dp)
                                .padding(7.dp)
                        ) {
                            items(item.item?.images?.size!!) { index ->
                                AsyncImage(
                                    model = item.item.images[index],
                                    contentDescription = " ",
                                    modifier = Modifier
                                        .height(200.dp).clip(RoundedCornerShape(15.dp)).width(160.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth(1f)) {
                            Row (Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 15.dp, top = 5.dp)
                                        .fillMaxWidth(0.7f)
                                ) {
                                    Text(
                                        text = if (item.item?.title!!.length <= 15) item.item.title else ("${
                                            item.item.title.substring(
                                                0,
                                                15
                                            )
                                        }..."), color = Color.Black, fontSize = 20.sp
                                    )
                                }
                                Box(modifier = Modifier.padding(top = 5.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .height(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                main
                                            ), contentAlignment = Alignment.Center
                                    ) {
                                        Row(Modifier.padding(start = 7.dp, end = 7.dp)) {
                                            Icon(
                                                imageVector = Icons.Filled.LocationOn,
                                                contentDescription = "",
                                                tint = Color.DarkGray,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = item.item?.buyerLocation!!,
                                                color = Color.DarkGray,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            ) {
                                Row {
                                    Spacer(modifier = Modifier.width(10.dp))
//                                    if (item.item?.exchangeable == "true") {
//                                        Box(
//                                            modifier = Modifier
//                                                .width(85.dp)
//                                                .height(20.dp)
//                                                .clip(RoundedCornerShape(4.dp))
//                                                .background(
//                                                    greenbg
//                                                ), contentAlignment = Alignment.Center
//                                        ) {
//                                            Row {
//                                                Icon(
//                                                    imageVector = Icons.Filled.Refresh,
//                                                    contentDescription = "",
//                                                    tint = betterWhite,
//                                                    modifier = Modifier.size(10.dp)
//                                                )
//                                                Text(
//                                                    text = "Exchangeable",
//                                                    color = betterWhite,
//                                                    fontSize = 8.sp
//                                                )
//                                            }
//                                        }
//                                    }

                                //                                    Box(
//                                        modifier = Modifier
//                                            .height(20.dp)
//                                            .fillMaxWidth()
//                                            .clip(RoundedCornerShape(4.dp))
//                                            .background(
//                                                greenbg
//                                            ), contentAlignment = Alignment.Center
//                                    ) {
//                                        Row(Modifier.padding(start = 5.dp, end = 5.dp)) {
//                                            Icon(
//                                                imageVector = Icons.Filled.LocationOn,
//                                                contentDescription = "",
//                                                tint = betterWhite,
//                                                modifier = Modifier.size(10.dp)
//                                            )
//                                            Text(
//                                                text = item.item?.buyerLocation!!,
//                                                color = betterWhite,
//                                                fontSize = 8.sp
//                                            )
//                                        }
//                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
    }
}

