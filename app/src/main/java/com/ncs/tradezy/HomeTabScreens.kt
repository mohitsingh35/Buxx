package com.ncs.tradezy

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.primary


@Composable
fun AllItems(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
    token:String,
    filteredList: ArrayList<RealTimeUserResponse>,
    navController: NavController
){

    Log.d("fcm token test", token.toString())
    if (token!="" && (filteredList.isNotEmpty())){
        updatefcmToken(itemState = filteredList[0] , viewModel = viewModel2, newToken = token)
    }

    Column(modifier = Modifier
        .background(primary)
        .fillMaxSize()){
        val user=viewModel2.res.value
        val res=viewModel.res.value
        val filter=ArrayList<EachAdResponse>()
        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        Box (Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)) {
            LazyColumn {
                items(1){
                    itemHolder(items = filter)
                }
            }
        }
    }
}
@Composable
fun BuyOnly(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
){



    Column(modifier = Modifier
        .background(primary)
        .fillMaxSize()){
        val user=viewModel2.res.value
        val res=viewModel.res.value
        val filter=ArrayList<EachAdResponse>()

        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid && (res.item[i].item?.exchangeable=="false")){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        Box (Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)) {
            LazyColumn {
                items(1){
                    itemHolder(items = filter)
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
        .background(primary)
        .fillMaxSize()){
        val user=viewModel2.res.value
        val res=viewModel.res.value
        val filter=ArrayList<EachAdResponse>()

        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!= FirebaseAuth.getInstance().currentUser?.uid && (res.item[i].item?.exchangeable=="true")){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        Box (Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)) {
            LazyColumn {
                items(1){
                    itemHolder(items = filter)
                }
            }
        }
    }
}

