package com.ncs.tradezy

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.secondary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(navController: NavController,viewModel:HomeScreenViewModel= hiltViewModel()){
    var text by remember {
        mutableStateOf("")
    }
    val res=viewModel.res.value
    val newList=ArrayList<EachAdResponse>()
    for (i in 0 until res.item.size){
        if (res.item[i].item?.sold!="true"){
            newList.add(res.item[i])
        }
    }
    val filterbyTrending=newList.sortedByDescending { it.item?.trendingViewCount?.toInt() }
    val filterbyViews=newList.sortedByDescending { it.item?.viewCount?.toInt() }

    Column(modifier = Modifier
        .background(primary)
        .padding(top = 30.dp, start = 10.dp, end = 10.dp)){
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .height(50.dp)
            .background(Color.Gray)
        ){
            TextField(value = text, onValueChange ={text=it}, placeholder = {
                Text(text = "Search", color = Color.LightGray, )
            } , trailingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "", tint = Color.LightGray)
            }, modifier = Modifier.fillMaxWidth())

        }
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn(){
            items(1){
                Text(text = "Trending Now \uD83D\uDCC8 :", fontSize = 16.sp, color = betterWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                LazyRow(){
                    items(1){
                        val size=filterbyTrending.size
                        if (size>=5){
                            for (i in 0 until 5){
                                eachAd(item = filterbyTrending[i])
                            }
                        }
                        if (size<5){
                            for (i in 0 until size){
                                eachAd(item = filterbyTrending[i])
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = "Most Viewed \uD83D\uDC40 :", fontSize = 16.sp, color = betterWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                LazyRow(){
                    items(1){
                        val size=filterbyViews.size
                        if (size>=5){
                            for (i in 0 until 5){
                                eachAd(item = filterbyViews[i])
                            }
                        }
                        if (size<5){
                            for (i in 0 until size){
                                eachAd(item = filterbyViews[i])
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}


@Composable
fun searchbar(navController: NavController){
    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .height(50.dp)
        .background(Color.Gray)
        .clickable { navController.navigate("Search") }
        .padding(start = 25.dp, top = 10.dp, bottom = 10.dp, end = 25.dp)
        ){
        Row (Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Search", color = Color.LightGray, fontSize = 20.sp)
            Icon(imageVector = Icons.Filled.Search, contentDescription = "", tint = Color.LightGray)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun eachAd(item:EachAdResponse) {
    val context= LocalContext.current
    var isClicked by remember {
        mutableStateOf(false)
    }
    if (isClicked){
        isClicked=false
        val intent = Intent(context, AdHostActivity::class.java)
        intent.putExtra("clickedItem", item)
        context.startActivity(intent)
    }
    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                isClicked = true
            }
            .border(width = 2.dp, color = secondary, shape = RoundedCornerShape(15.dp))
            .height(200.dp)
    ) {
        Column {
            AsyncImage(model = item.item?.images?.get(0)!!, contentDescription = "")
            Column (Modifier.padding(10.dp)){
                Text(text = item.item.title!!, color = betterWhite)
                Text(text = item.item.desc!!,color= betterWhite)
            }
        }
    }
    Spacer(modifier = Modifier.width(10.dp))
}
