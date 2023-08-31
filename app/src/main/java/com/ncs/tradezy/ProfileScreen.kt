package com.ncs.tradezy

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut:()->Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(userData?.profilePictureUrl!=null){
            AsyncImage(model = userData.profilePictureUrl, contentDescription = "",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(userData?.username!=null){
            Text(text = userData.username, textAlign = TextAlign.Center, fontSize =36.sp, fontWeight = FontWeight.Bold )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(onClick = onSignOut) {
            Text(text = "SignOut")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detailsEnterScreen(context: Context, viewmodel: AuthActivityViewModel = hiltViewModel(), navController: NavController){
    val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    var token =""
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("FCM token", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }
        token = task.result
    })
    val scope= rememberCoroutineScope()
    val userData= googleAuthUiClient.getSignedInUser()
    var username by remember {
        mutableStateOf(userData?.username)
    }
    var email by remember {
        mutableStateOf(userData?.email)
    }
    var phNum by remember {
        mutableStateOf("")
    }
    var move by remember {
        mutableStateOf(false)
    }
    val context= LocalContext.current

    if (move){
        move=false
        context.startActivity(Intent(context, MainActivity::class.java))
    }
    BackHandler {
        scope.launch {
            googleAuthUiClient.signOut()
        }
        navController.popBackStack()
    }

        Column(
            Modifier
                .padding(10.dp, top = 150.dp)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            AsyncImage(model = userData?.profilePictureUrl, contentDescription ="", modifier = Modifier
                .size(80.dp)
                .clip(
                    CircleShape
                ) )
            Spacer(modifier = Modifier.height(35.dp))

            OutlinedTextField(value = username!! , onValueChange = {username=it}, label = { Text(
                text = "Name"
            )})
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(value = email!! , onValueChange = {email=it}, label = { Text(
                text = "Email"
            )})
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(value = phNum!! , onValueChange = {phNum=it}, label = { Text(
                text = "Phone Number"
            )})
            Spacer(modifier = Modifier.height(15.dp))
            Button(onClick = { scope.launch(Dispatchers.Main) {
                viewmodel.insertUser(
                    RealTimeUserResponse.RealTimeUsers
                        (userId = userData?.userID,name = username,phNumber = phNum,profileDPurl = userData?.profilePictureUrl,email = email, fcmToken = token)).collect {
                    when (it) {
                        is ResultState.Success -> {
                            context.showMsg(
                                msg = it.data
                            )
                            move=true
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
            }}) {
                Text(text = "Submit")
            }
        }




}