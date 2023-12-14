package com.ncs.tradezy

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val TOPIC = "/topics/myTopic2"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val PREF_NAME = "user"
    val KEY_VARIABLE = "uid"
    val TAG = "MainActivity"
    private lateinit var connectivityObserver: ConnectivityObserver
    private var backPressedCount by mutableStateOf(0L)
    private var backPressedToast: Toast? = null
    var updater:AppConfigUpdater?=null
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
            var userList=ArrayList<String>()
            var isUserinDB by remember {
                mutableStateOf(false)
            }
            val profiles: ProfileActivityViewModel = hiltViewModel()
            val profilesres=profiles.res.value
            for (i in 0 until profilesres.item.size){
                userList.add(profilesres.item[i].item?.userId!!)
            }
            if (userList.contains(googleAuthUiClient.getSignedInUser()?.userID)){
                isUserinDB=true
            }
            val res2 = viewModel2.res.value
            val res3=viewModel3.res.value
//            var filtereNotiList = ArrayList<NotificationContent>()
            var filtereNotiList = mutableListOf<NotificationContent>()

            var promoNoti=ArrayList<NotificationContent>()
            var value by remember {
                mutableStateOf("")
            }
            var versionName by remember {
                mutableStateOf("")
            }
            val databaseReference = FirebaseDatabase.getInstance().reference.child("data").child("maintenance")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val key = snapshot.key
                        value = snapshot.getValue(String::class.java).toString()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            FirebaseDatabase.getInstance().reference.child("data").child("UpdateAvailable").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val key = snapshot.key
                        versionName = snapshot.getValue(String::class.java).toString()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })





            for (i in 0 until res3.item.size){
                promoNoti.add(res3.item[i])
            }
            var allNotiList=ArrayList<NotificationContent>()
//            for (i in 0 until res2.item.size){
//                if (res2.item[i].item?.receiverID==currentuser){
//                    filtereNotiList.add(res2.item[i])
//                }
//            }


            filtereNotiList.addAll(res2.item.filter { it.item?.receiverID == currentuser })


            val context= LocalContext.current
            if (promoNoti.isNotEmpty() || filtereNotiList.isNotEmpty()) {
                allNotiList.addAll(promoNoti)
                allNotiList.addAll(filtereNotiList)
            }
            var myallnoti: ArrayList<NotificationContent>
            val myallnoti1= allNotiList.sortedByDescending { it.item?.time }
            myallnoti= ArrayList(myallnoti1)
            noticount += myallnoti.count { notification ->
                notification.item?.read == "false" ||
                        (notification.item?.msgread?.get(currentuser) == "false" && notification.item?.read == "")
            }

            connectivityObserver= NetworkConnectivityObserver(LocalContext.current.applicationContext)
            val status by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable )
            val viewModel: ProfileActivityViewModel = hiltViewModel()
            val res=viewModel.res.value

            var filteredList= ArrayList<RealTimeUserResponse>()
            if (isUserinDB) {
                filteredList.addAll(res.item.filter { it.item?.userId == user })
            }
            else{
                filteredList.addAll(res.item)
            }

            val pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val uid=FirebaseAuth.getInstance().currentUser?.uid
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString(KEY_VARIABLE, uid)
            editor.apply()
            primaryTheme {
                if(status==ConnectivityObserver.Status.Available  && value=="" || versionName==""){
                    mainLoading()
                }
                if (status==ConnectivityObserver.Status.Available  && value=="false"){
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


                if (status!=ConnectivityObserver.Status.Available){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        internet()
                    }
                }
                if (value=="true"){
                    maintenance()
                }
                if (versionName> getCurrentAppVersion(this)){
                    appUpdater()
                }
            }
        }
    }
    fun getCurrentAppVersion(context: Context): String {
        try {
            val packageManager: PackageManager = context.packageManager
            val packageName: String = context.packageName

            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("VersionInfo", "Package name not found", e)
        }
        return "N/A"
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









