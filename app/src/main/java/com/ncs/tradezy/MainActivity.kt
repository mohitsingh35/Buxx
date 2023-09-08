package com.ncs.tradezy

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.tradezy.networkObserver.ConnectivityObserver
import com.ncs.tradezy.networkObserver.NetworkConnectivityObserver
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.HostelTheme
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.primaryTheme
import dagger.hilt.android.AndroidEntryPoint

const val TOPIC = "/topics/myTopic2"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val PREF_NAME = "user"
    val KEY_VARIABLE = "uid"
    val TAG = "MainActivity"
    private lateinit var connectivityObserver: ConnectivityObserver
    private var backPressedCount by mutableStateOf(0L)
    private var backPressedToast: Toast? = null

    val user=FirebaseAuth.getInstance().currentUser?.uid
    @OptIn(ExperimentalPagerApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        var token =""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result
        })

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        super.onCreate(savedInstanceState)
        setContent {

            connectivityObserver= NetworkConnectivityObserver(LocalContext.current.applicationContext)
            val status by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable )
            val viewModel: ProfileActivityViewModel = hiltViewModel()
            val res=viewModel.res.value
            var filteredList= ArrayList<RealTimeUserResponse>()


                for (i in 0 until res.item.size){
                    if (res.item[i].item?.userId== user){
                        filteredList.add(res.item[i])
                    }
                }


            val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val uid=FirebaseAuth.getInstance().currentUser?.uid
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString(KEY_VARIABLE, uid)
            editor.apply()
            primaryTheme {
                if (status==ConnectivityObserver.Status.Available){
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(primary)){
                        val navController= rememberNavController()
                        Column(modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 18.dp)) {

                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.93f)){
                                if (filteredList.isEmpty()){
                                    Navigation(navController = navController,applicationContext,token)
                                }
                                else{
                                    Navigation(navController = navController,applicationContext,token,filteredList)
                                }
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
                else{
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        Text(text = "Internet Error")
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









