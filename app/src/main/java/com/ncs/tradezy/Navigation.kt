package com.ncs.tradezy

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ncs.tradezy.repository.RealTimeUserResponse

@Composable
fun Navigation(
    navController: NavHostController,
    context: Context,
    token: String, filterList: ArrayList<RealTimeUserResponse> = ArrayList()){
    val context= LocalContext.current
    NavHost(navController = navController, startDestination = "Home" ){
        composable("Home"){
            HomeScreen(token = token, filteredList = filterList)
        }
        composable("Add"){
            AddScreen(appContext = context)
        }
        composable("Search"){
            SearchScreen()
        }

    }
}
