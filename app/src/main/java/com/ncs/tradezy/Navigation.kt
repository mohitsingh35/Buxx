package com.ncs.tradezy

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigation(navController: NavHostController,context: Context){
    val context= LocalContext.current
    NavHost(navController = navController, startDestination = "Home" ){
        composable("Home"){
            HomeScreen()
        }
        composable("Add"){
            AddScreen(appContext = context)
        }
        composable("Search"){
            SearchScreen()
        }

    }
}
