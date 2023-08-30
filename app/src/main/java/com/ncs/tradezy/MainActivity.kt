package com.ncs.tradezy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.ui.theme.HostelTheme
import com.ncs.tradezy.ui.theme.primary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val PREF_NAME = "user"
    val KEY_VARIABLE = "uid"
    private var backPressedCount by mutableStateOf(0L)
    private var backPressedToast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val uid=FirebaseAuth.getInstance().currentUser?.uid
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString(KEY_VARIABLE, uid)
            editor.apply()
            HostelTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(primary)){
                    val navController= rememberNavController()
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 18.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.93f)){
                            Navigation(navController = navController,applicationContext)
                        }
                        bottomBar(items = listOf(
                            BottomBarContent(R.drawable.home,"Home"),
                            BottomBarContent(R.drawable.add,"Add"),
                            BottomBarContent(R.drawable.search,"Search")
                        ),
                            navController = navController, onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                }
            }
        }
    }
    override fun onBackPressed() {
        if (backPressedCount == 1L) {
            backPressedToast?.cancel()
            finishAffinity()

        } else {
            backPressedCount++
            backPressedToast?.cancel()
            backPressedToast = Toast.makeText(
                this,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            )
            backPressedToast?.show()
        }
    }
}








