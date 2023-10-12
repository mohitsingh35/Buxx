package com.ncs.tradezy.googleAuth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ncs.tradezy.AuthViewModel
import com.ncs.tradezy.MainActivity
import com.ncs.tradezy.ProfileScreen
import com.ncs.tradezy.detailsEnterScreen
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.internet
import com.ncs.tradezy.maintenance
import com.ncs.tradezy.networkObserver.ConnectivityObserver
import com.ncs.tradezy.networkObserver.NetworkConnectivityObserver
import com.ncs.tradezy.ui.theme.primaryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoogleAuthActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        connectivityObserver=NetworkConnectivityObserver(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {

            primaryTheme {


                val status by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Unavailable )
                var scope= rememberCoroutineScope()
                val viewModel2: AuthViewModel = hiltViewModel()
                val res=viewModel2.res.value
                var uid = ""
                if (status==ConnectivityObserver.Status.Available){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        val context= LocalContext.current
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "splash") {
                            composable("sign_in") {
                                val viewModel = viewModel<GoogleSignInViewModel>()
                                val state by viewModel.state.collectAsState()

                                for (i in 0 until  res.item.size){
                                    if (res.item[i].item?.userId==googleAuthUiClient.getSignedInUser()?.userID){
                                        uid = res.item[i].item?.userId!!
                                    }
                                }

                                LaunchedEffect(key1 = Unit) {
                                    if(googleAuthUiClient.getSignedInUser() != null) {
                                        context.startActivity(Intent(context, MainActivity::class.java))
                                    }
                                }

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if(result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthUiClient.signInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if(state.isSignInSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Sign in successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        if (uid==""){
                                            uid=""
                                            navController.navigate("detailsEnter")

                                        }
                                        if (uid!=""){
                                            uid=""
                                            context.startActivity(Intent(context, MainActivity::class.java))

                                        }
                                        viewModel.resetState()
                                    }
                                }

                                googleScreen(
                                    state = state,
                                    navController=navController,
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    }
                                )
                            }
                            composable("profile") {
                                ProfileScreen(
                                    userData = googleAuthUiClient.getSignedInUser(),
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Signed out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            }
                            composable("about2"){
                                val viewModel = viewModel<GoogleSignInViewModel>()
                                val state by viewModel.state.collectAsState()

                                for (i in 0 until  res.item.size){
                                    if (res.item[i].item?.userId==googleAuthUiClient.getSignedInUser()?.userID){
                                        uid = res.item[i].item?.userId!!
                                    }
                                }

                                LaunchedEffect(key1 = Unit) {
                                    if(googleAuthUiClient.getSignedInUser() != null) {
                                        context.startActivity(Intent(context, MainActivity::class.java))
                                    }
                                }

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if(result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthUiClient.signInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if(state.isSignInSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Sign in successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        if (uid==""){
                                            uid=""
                                            navController.navigate("detailsEnter")

                                        }
                                        if (uid!=""){
                                            uid=""
                                            context.startActivity(Intent(context, MainActivity::class.java))

                                        }
                                        viewModel.resetState()
                                    }
                                }

                                about2(navController = navController, onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                })
                            }
                            composable("about3"){

                                about3(navController=navController)
                            }
                            composable("detailsEnter"){
                                detailsEnterScreen(applicationContext, navController = navController)
                            }
                            composable("splash"){
                                var isSigned by remember {
                                    mutableStateOf(false)
                                }
                                LaunchedEffect(key1 = Unit) {
                                    if(googleAuthUiClient.getSignedInUser() != null) {
                                        isSigned=true
                                    }
                                }
                                splash(navController = navController,isSigned)
                            }
                        }
                    }
                }
                if (status!=ConnectivityObserver.Status.Available){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        internet()
                    }
                }
            }
            }

        }

}

