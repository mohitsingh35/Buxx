package com.ncs.tradezy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.secondary

@Composable
fun itemHolder(items:List<ItemContent>, intitalSelectedItem: Int=0){
    var clicked by remember {
        mutableStateOf(intitalSelectedItem)
    }
    var showToast by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, bannerContent ->
            eachItem(item = bannerContent, index = index) {
                clicked=it
                showToast=true
            }

        }
        if (showToast){
            LocalContext.current.showMsg("Clicked $clicked ")
            showToast=false
        }
    }

}

@Composable
fun eachItem(
    item: ItemContent,
    index:Int,
    onItemClick:(Int)-> Unit
){
    val  context= LocalContext.current
    Column(Modifier.padding(bottom = 10.dp)) {
        Box (modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(index) }
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
                Image(
                    painterResource(id = item.image[0]),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp), contentDescription = "image", contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 10.dp)){
                    Text(text = item.title, color = accent, fontSize = 24.sp)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, top = 5.dp)){
                    Text(text = item.desc, color = betterWhite, fontSize = 13.sp)
                }
            }
        }
    }
}

