package com.ncs.tradezy

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val currentUser=FirebaseAuth.getInstance().currentUser?.uid

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel= hiltViewModel(),
    viewModel2: ProfileActivityViewModel= hiltViewModel(),
    token:String,
    filteredList: ArrayList<RealTimeUserResponse>
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
        val context= LocalContext.current
        val scope= rememberCoroutineScope()

        val items= listOf(
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,""),
            ItemContent(listOf( R.drawable.amg),"Lamborghini","Lamborghini",9000,1692814788107,true,""),
            ItemContent(listOf( R.drawable.amg),"Buggati","Lamborghini",9000,1692814788110,true,""),
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,""),
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,"")
        )
        val newList = items.sortedByDescending { it.time  }
        val filter=ArrayList<EachAdResponse>()
        for (i in 0 until res.item.size){
            if (res.item[i].item?.sellerId!=FirebaseAuth.getInstance().currentUser?.uid){
                filter.add(res.item[i])
            }
            else{
                continue
            }
        }
        setActionBar(screenName = "Home", image = R.drawable.ic_launcher_foreground)
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
fun SearchScreen(){
    Column(modifier = Modifier.background(primary)){
        setActionBar(screenName = "Search", image = R.drawable.ic_launcher_foreground)
    }
}
@Composable
fun updatefcmToken(newToken:String?, viewModel: ProfileActivityViewModel, itemState: RealTimeUserResponse){

    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    LaunchedEffect(true ) {
        delay(5000L)
        scope.launch(Dispatchers.Main) {
            viewModel.update(
                RealTimeUserResponse(
                    item = RealTimeUserResponse.RealTimeUsers(name = itemState.item?.name,fcmToken = newToken, email = itemState.item?.email, phNumber = itemState.item?.phNumber),
                    key = itemState.key
                )
            ).collect {
                when (it) {
                    is ResultState.Success -> {
                        context.showMsg(
                            msg = ""
                        )
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
        }
    }
}
