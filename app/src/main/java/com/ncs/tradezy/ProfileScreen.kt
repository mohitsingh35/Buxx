package com.ncs.tradezy

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.main
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
    var save by remember {
        mutableStateOf(false)
    }
    if (save){
        save=false
        LaunchedEffect(key1 = true ){
            scope.launch(Dispatchers.Main) {
                viewmodel.insertUser(
                    RealTimeUserResponse.RealTimeUsers
                        (
                        userId = userData?.userID,
                        name = username,
                        phNumber = phNum,
                        profileDPurl = userData?.profilePictureUrl,
                        email = email,
                        fcmToken = token
                    )
                ).collect {
                    when (it) {
                        is ResultState.Success -> {
                            context.showMsg(
                                msg = it.data
                            )
                            move = true
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
    Box (modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.TopCenter){
        Column {
            Spacer(modifier = Modifier.height(30.dp))
            Column (Modifier.padding(20.dp)){
                Text(
                    text = "We swear this is last 👻 ",
                    color = Color.Gray,
                    fontWeight = FontWeight.Thin,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "Have one final look!", color = Color.Gray,
                    fontWeight = FontWeight.Thin,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                Modifier
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AsyncImage(
                    model = userData?.profilePictureUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.height(35.dp))

                OutlinedTextField(value = username!!, onValueChange = { username = it }, label = {
                    Text(
                        text = "Name"
                    )
                }, shape = RoundedCornerShape(15.dp), maxLines = 1,
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(value = phNum!!, onValueChange = { phNum = it }, label = {
                    Text(
                        text = "Phone : +91"
                    )
                },shape = RoundedCornerShape(15.dp), maxLines = 1, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(value = email!!, onValueChange = { email = it }, label = {
                    Text(
                        text = "Email"
                    )
                }, enabled = false,shape = RoundedCornerShape(15.dp))
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "*Psst!, you can always update these later", color = Color.Gray,
                    fontWeight = FontWeight.Thin,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                Box(Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {if (username!!.isNotEmpty() && phNum.length==10 && phNum!!.isNotEmpty()){
                        save=true
                    }
                    else{
                        context.showMsg("Fill all details")
                    }}
                    .background(main), contentAlignment = Alignment.Center) {
                    Row {
                        Text(text = "Register", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(25.dp) )
                    }
                }

            }
        }

    }

}