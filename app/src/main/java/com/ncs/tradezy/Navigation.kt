package com.ncs.tradezy

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ncs.tradezy.repository.RealTimeUserResponse

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavHostController,
    context: Context,
    token: String, filterList: ArrayList<RealTimeUserResponse> = ArrayList()){
    val context= LocalContext.current
    NavHost(navController = navController, startDestination = "Home" ){
        composable("Home"){
            HomeScreen(token = token, filteredList = filterList, navController = navController)
        }
        composable("Add"){
            AddScreen(appContext = context, navController = navController)
        }
        composable("Search"){
            SearchScreen(navController1 = navController)
        }
        composable("notificationScreen"){
            notificationsScreen(navController = navController)
        }

    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationChatHost(
    navController: NavHostController,
    name:String,id:String,fcmtoken:String,dp:String){
    val context= LocalContext.current
    NavHost(navController = navController, startDestination = "chatHost" ){
        composable("chatHost"){
            chatHost(name =  name!! , id = id!!, fcmtoken = fcmtoken!!, dp = dp!! )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationSearchScreen(
    navController: NavHostController, filterbyTrending: List<EachAdResponse>, filterbyViews:List<EachAdResponse>){
    val context= LocalContext.current
    NavHost(navController = navController, startDestination = "discover" ){
        composable("discover"){
            discoverarea(filterbyTrending = filterbyTrending, filterbyViews = filterbyViews,navController)
        }
        composable("search"){
            searcharea(navController)
        }
    }
}
