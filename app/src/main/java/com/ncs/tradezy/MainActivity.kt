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
import com.bitpolarity.bitscuit.Bitscuit
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.networkObserver.ConnectivityObserver
import com.ncs.tradezy.networkObserver.NetworkConnectivityObserver
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.HostelTheme
import com.ncs.tradezy.ui.theme.background
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
    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

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
            var noticount = 0
            val viewModel2: NotificationViewModel= hiltViewModel()
            val viewModel3: PromoNotificationViewModel= hiltViewModel()
            val currentuser = FirebaseAuth.getInstance().currentUser?.uid
            val res2 = viewModel2.res.value
            val res3=viewModel3.res.value
            var filtereNotiList = ArrayList<NotificationContent>()
            var promoNoti=ArrayList<NotificationContent>()
            for (i in 0 until res3.item.size){
                promoNoti.add(res3.item[i])
            }
            Log.d("promo",promoNoti.toString())
            var allNotiList=ArrayList<NotificationContent>()

            for (i in 0 until res2.item.size){
                if (res2.item[i].item?.receiverID==currentuser){
                    filtereNotiList.add(res2.item[i])
                }
            }

            val context= LocalContext.current
            if (promoNoti.isNotEmpty() || filtereNotiList.isNotEmpty()) {
                allNotiList.addAll(promoNoti)
                allNotiList.addAll(filtereNotiList)
            }
            var myallnoti: ArrayList<NotificationContent>
            val myallnoti1= allNotiList.sortedByDescending { it.item?.time }
            myallnoti= ArrayList(myallnoti1)
            for (i in 0 until myallnoti.size) {
                if (myallnoti[i].item?.read == "false") {
                    noticount++
                }
                if (myallnoti[i].item?.msgread?.containsKey(currentuser)==false && myallnoti[i].item?.read == ""){
                    noticount++
                }
                if (myallnoti[i].item?.msgread?.containsKey(currentuser)==true && myallnoti[i].item?.read == ""){
                    if (myallnoti[i].item?.msgread?.getValue(currentuser!!)=="false"){
                        noticount++
                    }
                }

            }
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
                    if (filteredList.isEmpty()){
                        mainLoading()
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(background)
                        ) {
                            val navController = rememberNavController()
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(bottom = 10.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.92f)
                                ) {
                                    if (filteredList.isEmpty()) {
                                        Navigation(
                                            navController = navController,
                                            applicationContext,
                                            token
                                        )
                                    } else {
                                        Navigation(
                                            navController = navController,
                                            applicationContext,
                                            token,
                                            filteredList
                                        )
                                    }
                                }
                                bottomBar(items = listOf(
                                    BottomBarContent(R.drawable.home_ic, "Home"),
                                    BottomBarContent(R.drawable.search_ic, "Search"),
                                    BottomBarContent(R.drawable.add_ic, "Add"),
                                    BottomBarContent(
                                        R.drawable.notifications_ic,
                                        "notificationScreen"
                                    ),
                                    BottomBarContent(R.drawable.person_ic, "profile")
                                ),
                                    navController = navController,
                                    onItemClick = {
                                        navController.navigate(it.route)
                                    },
                                    googleAuthUIClient = googleAuthUiClient,
                                    noticount = noticount
                                )
                            }
                        }
                    }
                }
                else{
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        internet()
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









