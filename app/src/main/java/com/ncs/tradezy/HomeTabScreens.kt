package com.ncs.tradezy

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.networkObserver.ConnectivityObserver
import com.ncs.tradezy.networkObserver.NetworkConnectivityObserver
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.primary


@Composable
fun AllItems(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
    token:String,
    filteredList: ArrayList<RealTimeUserResponse>,
    navController: NavController
){

    
        if (token!="" && (filteredList.isNotEmpty())){
            updatefcmToken(itemState = filteredList[0] , viewModel = viewModel2, newToken = token)
        }

        Column(modifier = Modifier
            .background(background)
            .fillMaxSize()){
            val user=viewModel2.res.value
            val res=viewModel.res.value
            val filter=ArrayList<EachAdResponse>()
            for (i in 0 until res.item.size){
                if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid && (res.item[i].item?.sold=="false")){
                    filter.add(res.item[i])
                }
                else{
                    continue
                }
            }
            val filter2=filter.sortedByDescending { it.item?.time ?: 0  }
            if (filter.isEmpty()){
                emptyscreen()
            }
            else {
                Box() {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
//                    LazyColumn {
//                        item {
//                            LazyRow{
//                                items(5){
//                                    slider()
//                                }
//                            }
//                        }
//                        item{
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                        items(1) {
//                            itemHolder(items = filter)
//                        }
//                    }
                        val gridHeight: Dp
                        val totalItems = filter.size
                        if (totalItems % 2 != 0) {
                            gridHeight = with(LocalDensity.current) {
                                val gridHeightDp = (totalItems + 1) / 2 * 300.dp
                                gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                            }
                        } else {
                            gridHeight = with(LocalDensity.current) {
                                val gridHeightDp = totalItems / 2 * 300.dp
                                gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                            }
                        }
                        LazyColumn() {
                            item {
                                LazyRow(Modifier.padding(start = 5.dp)) {
                                    items(3) {
                                        slider()
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    userScrollEnabled = false,
                                    modifier = Modifier.height(gridHeight),
                                    content = {
                                        items(filter2.size) { index ->
                                            eachItem(
                                                item = filter2[index],
                                                index = index,
                                                onItemClick = {

                                                })
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    
}

@Composable
fun slider(){
    Box(modifier = Modifier
        .width(350.dp)
        .height(150.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(Color.Black)
        , contentAlignment = Alignment.Center
        ){
        Text(text = "Promotional slider content", color = betterWhite)
    }
    Spacer(modifier = Modifier.width(5.dp))
}

@Composable
fun BuyOnly(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
){
    Column(modifier = Modifier
        .background(background)
        .fillMaxSize()){
        val user=viewModel2.res.value
        val res=viewModel.res.value
        val filter=ArrayList<EachAdResponse>()
        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid && (res.item[i].item?.sold=="false") &&
                (res.item[i].item?.exchangeable=="false")){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        val filter2=filter.sortedByDescending { it.item?.time ?: 0 }
        if (filter.isEmpty()){
            emptyscreen()
        }
        else {
            Box() {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
//                    LazyColumn {
//                        item {
//                            LazyRow{
//                                items(5){
//                                    slider()
//                                }
//                            }
//                        }
//                        item{
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                        items(1) {
//                            itemHolder(items = filter)
//                        }
//                    }
                    val gridHeight: Dp
                    val totalItems = filter.size
                    if (totalItems % 2 != 0) {
                        gridHeight = with(LocalDensity.current) {
                            val gridHeightDp = (totalItems + 1) / 2 * 300.dp
                            gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                        }
                    } else {
                        gridHeight = with(LocalDensity.current) {
                            val gridHeightDp = totalItems / 2 * 300.dp
                            gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                        }
                    }
                    LazyColumn() {
                        item {
                            LazyRow(Modifier.padding(start = 5.dp)) {
                                items(3) {
                                    slider()
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                userScrollEnabled = false,
                                modifier = Modifier.height(gridHeight),
                                content = {
                                    items(filter2.size) { index ->
                                        eachItem(
                                            item = filter2[index],
                                            index = index,
                                            onItemClick = {

                                            })
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun Exchange(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
){
    Column(modifier = Modifier
        .background(background)
        .fillMaxSize()){
        val user=viewModel2.res.value
        val res=viewModel.res.value
        val filter=ArrayList<EachAdResponse>()
        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid && (res.item[i].item?.sold=="false") &&
                (res.item[i].item?.exchangeable=="true")){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        val filter2=filter.sortedByDescending { it.item?.time ?: 0 }

        if (filter.isEmpty()){
            emptyscreen()
        }
        else {
            Box() {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
//                    LazyColumn {
//                        item {
//                            LazyRow{
//                                items(5){
//                                    slider()
//                                }
//                            }
//                        }
//                        item{
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                        items(1) {
//                            itemHolder(items = filter)
//                        }
//                    }
                    val gridHeight: Dp
                    val totalItems = filter.size
                    if (totalItems % 2 != 0) {
                        gridHeight = with(LocalDensity.current) {
                            val gridHeightDp = (totalItems + 1) / 2 * 300.dp
                            gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                        }
                    } else {
                        gridHeight = with(LocalDensity.current) {
                            val gridHeightDp = totalItems / 2 * 300.dp
                            gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                        }
                    }
                    LazyColumn() {
                        item {
                            LazyRow(Modifier.padding(start = 5.dp)) {
                                items(3) {
                                    slider()
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                userScrollEnabled = false,
                                modifier = Modifier.height(gridHeight),
                                content = {
                                    items(filter2.size) { index ->
                                        eachItem(
                                            item = filter2[index],
                                            index = index,
                                            onItemClick = {

                                            })
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

