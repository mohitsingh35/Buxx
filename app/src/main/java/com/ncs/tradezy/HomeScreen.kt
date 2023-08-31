package com.ncs.tradezy

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.secondary
import java.io.Serializable

@Composable
fun itemHolder(items:List<EachAdResponse>, intitalSelectedItem: EachAdResponse?=null){
    var clicked by remember {
        mutableStateOf(intitalSelectedItem)
    }
    val context= LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
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
){
    val  context= LocalContext.current
    Column(Modifier.padding(bottom = 10.dp)) {
        Box (modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item)
                val intent = Intent(context, AdHostActivity::class.java)
                intent.putExtra("clickedItem", item)
                context.startActivity(intent) }
            .border(
                border = BorderStroke(
                    3.dp,
                    secondary
                ), shape = RoundedCornerShape(15.dp)
            )

            .clip(RoundedCornerShape(15.dp))
            .height(350.dp)
        ){
            Column {
                LazyRow(
                    Modifier.fillMaxWidth().height(250.dp)
                ) {
                    items(item.item?.images?.size!!) { index ->
                        AsyncImage(model = item.item.images[index], contentDescription =  " ",modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp).padding(end = 5.dp), contentScale = ContentScale.Crop)
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 10.dp)){
                    Text(text = item.item?.title!!, color = accent, fontSize = 24.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 5.dp)){
                    Text(text = item.item?.desc!!, color = betterWhite, fontSize = 13.sp)
                }
            }
        }
    }
}

