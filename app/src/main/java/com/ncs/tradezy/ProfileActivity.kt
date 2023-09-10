package com.ncs.tradezy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.googleAuth.GoogleAuthActivity
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.primaryTheme

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var token =""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM token profile", "")
                return@OnCompleteListener
            }
            token = task.result
        })
        super.onCreate(savedInstanceState)
        setContent {
            primaryTheme {
                val navController= rememberNavController()
                val viewModel: ProfileActivityViewModel = hiltViewModel()
                var userList=ArrayList<String>()
                val res=viewModel.res.value
                var isUserinDB by remember {
                    mutableStateOf(false)
                }
                for (i in 0 until res.item.size){
                    userList.add(res.item[i].item?.userId!!)
                }
                if (userList.contains(googleAuthUiClient.getSignedInUser()?.userID)){
                    isUserinDB=true
                }
                if (userList.isEmpty()){
                    loading()
                }
                else{
                    NavigationUserProfileScreen(navController = navController, isUserinDB = isUserinDB,token=token,googleAuthUiClient)
                }
            }
        }
    }

    override fun onBackPressed() {
        this.startActivity(Intent(this,MainActivity::class.java))
    }


}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUserProfile(isUserinDB:Boolean,viewModel: ProfileActivityViewModel = hiltViewModel(),token:String,googleAuthUiClient:GoogleAuthUIClient,navController: NavController){
    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    val userData= googleAuthUiClient.getSignedInUser()
    val res=viewModel.res.value
    val currentUser=userData?.userID
    var filteredList:List<RealTimeUserResponse>?=null
    val isUpdate = remember {
        mutableStateOf(false)
    }
    val createNewUserinDb = remember {
        mutableStateOf(false)
    }
    if (isUserinDB) {
        filteredList = res.item.filter { userResponse ->
            userResponse.item?.userId == currentUser
        }
    }
    if (res.item.isNotEmpty()!!) {
        if (isUpdate.value) {
            updateUser(
                isUpdate = isUpdate,
                itemState = filteredList?.get(0)!!,
                viewModel = viewModel,
                fcmToken = token
            )
        }
        if (createNewUserinDb.value) {
            CreateNewUserinDb(
                isUpdate = createNewUserinDb,
                viewModel = viewModel,
                googleAuthUiClient = googleAuthUiClient,
                fcmToken = token
            )
        }
        var udata = filteredList?.get(0)?.item
        if (isUserinDB) {
            var username by remember {
                mutableStateOf(udata?.name)
            }
            var email by remember {
                mutableStateOf(udata?.email)
            }
            var phNum by remember {
                mutableStateOf(udata?.phNumber)
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(background)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier
                        .padding(start = 28.dp)
                        .clip(CircleShape)
                        .clickable {
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 90.dp)
                            .clip(CircleShape), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Profile",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .background(background), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(1) {
                        ProfileScreenContent(
                            profileUrl = userData?.profilePictureUrl,
                            username = username,
                            email = email,
                            phNum = phNum,
                            googleAuthUiClient = googleAuthUiClient,
                            onClick = {
                                isUpdate.value = true
                            },
                            navController = navController
                        )
                    }

                }
            }
        } else {
            var username by remember {
                mutableStateOf(userData?.username)
            }
            var email by remember {
                mutableStateOf(userData?.email)
            }
            var phNum by remember {
                mutableStateOf(userData?.phNum)
            }
            Column {
                ProfileScreenContent(
                    profileUrl = userData?.profilePictureUrl,
                    username = username,
                    email = email,
                    phNum = phNum,
                    googleAuthUiClient = googleAuthUiClient,
                    onClick = {
                        createNewUserinDb.value = true
                    },
                    navController = navController
                )
            }


        }
    }
    else{
        loading()
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreenContent(profileUrl:String?,username:String?,email:String?,phNum:String?,viewModel: HomeScreenViewModel= hiltViewModel(),onClick:()->Unit,googleAuthUiClient:GoogleAuthUIClient,navController: NavController){
    val res=viewModel.res.value
    val scope= rememberCoroutineScope()
    val userads=ArrayList<EachAdResponse>()


    Column(modifier = Modifier
        .fillMaxSize()
        .background(background)
        .padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {


        Spacer(modifier = Modifier.height(30.dp))

        AsyncImage(model = profileUrl, contentDescription ="", modifier = Modifier
            .size(80.dp)
            .clip(
                CircleShape
            ) )
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Hi ${username}!", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Light)

        Spacer(modifier = Modifier.height(35.dp))
        Row (
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(end = 40.dp), horizontalArrangement = Arrangement.End){
            Row (
                Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { onClick() }, verticalAlignment = Alignment.CenterVertically){
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "", tint = Color.Gray)
                Text(text = "Edit", color = Color.Gray, fontSize = 14.sp)
            }
        }
        OutlinedTextField(value = username!!, onValueChange = {  }, label = {
            Text(
                text = "Name"
            )
        }, readOnly = true,shape = RoundedCornerShape(15.dp), maxLines = 1,
        )
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(value = email!!, onValueChange = { }, label = {
            Text(
                text = "Email"
            )
        }, readOnly = true,shape = RoundedCornerShape(15.dp), maxLines = 1,
        )
        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(value = phNum!!, onValueChange = { }, label = {
            Text(
                text = "Phone Number"
            )
        }, readOnly = true, shape = RoundedCornerShape(15.dp), maxLines = 1,
        )
        Spacer(modifier = Modifier.height(15.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp), horizontalAlignment = Alignment.Start){
            Text(
                text = "Account",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("ads") }
                    .height(60.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row (Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){
                    Image(painter = painterResource(id = R.drawable.myads), contentDescription = "",Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = "My Ads & Listings",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "All Ads, Active Ads, Sold",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("help") }
                    .height(60.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row (Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){
                    Image(painter = painterResource(id = R.drawable.help), contentDescription = "",Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = "Help",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "About & Terms of Use",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
            }
            val context= LocalContext.current
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            googleAuthUiClient.signOut()
                            navigateToSignInActivity(context)
                        }
                    }
                    .height(60.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row (Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "",Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Column {
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "Sad to see you go",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Thin
                        )
                    }
                }
                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
            }
        }



    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun updateUser(
    isUpdate: MutableState<Boolean>,
    itemState: RealTimeUserResponse,
    viewModel: ProfileActivityViewModel,
    fcmToken:String,
){
    val username= remember {
        mutableStateOf(itemState.item?.name)
    }
    val email= remember {
        mutableStateOf(itemState.item?.email)
    }
    val phNum= remember {
        mutableStateOf(itemState.item?.phNumber)
    }

    val focusRequester = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    if(isUpdate.value){
        AlertDialog(onDismissRequest = { isUpdate.value=false }, confirmButton = {
            Button(onClick = { scope.launch(Dispatchers.Main) {
                viewModel.update(
                    RealTimeUserResponse(item = RealTimeUserResponse.RealTimeUsers(name=username.value, email = email.value, phNumber = phNum.value, fcmToken = fcmToken),key = itemState.key)
                ).collect{
                    when(it){
                        is ResultState.Success->{
                            context.showMsg(
                                msg=it.data
                            )
                            isUpdate.value=false
                            recreateActivity(context)

                        }
                        is ResultState.Failure->{
                            context.showMsg(
                                msg=it.msg.toString()
                            )
                        }
                        ResultState.Loading->{
                        }
                    }
                }
            }}, colors = ButtonDefaults.buttonColors(containerColor = main, contentColor = betterWhite)) {
                Text(text = "Update")
            }
        }, text = {
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Update", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = username.value!!,
                    onValueChange = {
                        username.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                        }
                    ),
                    label = {
                        Text(text = "Name")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email.value!!,
                    onValueChange = {
                        email.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                        }
                    ),
                    label = {
                        Text(text = "Email")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phNum.value!!,
                    onValueChange = {
                        phNum.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal).copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            scope.launch(Dispatchers.Main) {
                                viewModel.update(
                                    RealTimeUserResponse(item = RealTimeUserResponse.RealTimeUsers(name=username.value, email = email.value, phNumber = phNum.value, fcmToken = fcmToken),key = itemState.key)
                                ).collect{
                                    when(it){
                                        is ResultState.Success->{
                                            context.showMsg(
                                                msg=it.data
                                            )
                                            isUpdate.value=false
                                            recreateActivity(context)

                                        }
                                        is ResultState.Failure->{
                                            context.showMsg(
                                                msg=it.msg.toString()
                                            )
                                        }
                                        ResultState.Loading->{
                                        }
                                    }
                                }
                            }
                        }
                    ),
                    label = {
                        Text(text = "Phone Number")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        )
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewUserinDb(
    isUpdate: MutableState<Boolean>,
    viewModel: ProfileActivityViewModel,
    googleAuthUiClient: GoogleAuthUIClient,
    fcmToken:String
    ){
    val data=googleAuthUiClient.getSignedInUser()
    val username= remember {
        mutableStateOf(data?.username)
    }
    val email= remember {
        mutableStateOf(data?.email)
    }
    val phNum= remember {
        mutableStateOf(data?.phNum)
    }
    val focusRequester = remember { FocusRequester() }

    val scope= rememberCoroutineScope()
    val context= LocalContext.current
    if(isUpdate.value){
        AlertDialog(onDismissRequest = { isUpdate.value=false }, confirmButton = {
            Button(onClick = { scope.launch(Dispatchers.Main) {
                viewModel.insertUser(
                    RealTimeUserResponse.RealTimeUsers
                        (userId = data?.userID,name = username.value,phNumber = phNum.value,profileDPurl = data?.profilePictureUrl,email = email.value, fcmToken = fcmToken)).collect {
                    when (it) {
                        is ResultState.Success -> {
                            context.showMsg(
                                msg = it.data
                            )
                            isUpdate.value=false
                            recreateActivity(context)
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
            }},colors = ButtonDefaults.buttonColors(containerColor = main, contentColor = betterWhite)) {
                Text(text = "Update")
            }
        }, text = {
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Update", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = username.value!!,
                    onValueChange = {
                        username.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                        }
                    ),
                    label = {
                        Text(text = "Name")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email.value!!,
                    onValueChange = {
                        email.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                        }
                    ),
                    label = {
                        Text(text = "Email")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phNum.value!!,
                    onValueChange = {
                        phNum.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal).copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            scope.launch(Dispatchers.Main) {
                                viewModel.insertUser(
                                    RealTimeUserResponse.RealTimeUsers
                                        (userId = data?.userID,name = username.value,phNumber = phNum.value,profileDPurl = data?.profilePictureUrl,email = email.value)).collect {
                                    when (it) {
                                        is ResultState.Success -> {
                                            context.showMsg(
                                                msg = it.data
                                            )
                                            isUpdate.value=false
                                            recreateActivity(context)
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
                    ),
                    label = {
                        Text(text = "Phone Number")
                    },
                    shape = RoundedCornerShape(15.dp),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        )
    }

}

fun navigateToSignInActivity(context:Context) {
    val intent = Intent(context, GoogleAuthActivity::class.java)
    intent.flags =
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

fun recreateActivity(context:Context) {
    val intent = Intent(context, ProfileActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun adsPage(navController: NavController,viewModel: HomeScreenViewModel= hiltViewModel()){
    val context= LocalContext.current
    val res=viewModel.res.value
    val scope= rememberCoroutineScope()
    val userads=ArrayList<EachAdResponse>()
    for (i in 0 until res.item.size){
        if (res.item[i].item?.sellerId==FirebaseAuth.getInstance().currentUser?.uid){
            userads.add(res.item[i])
        }
    }
    val activeAds=ArrayList<EachAdResponse>()
    for (i in 0 until userads.size){
        if (userads[i].item?.sold=="false"){
            activeAds.add(userads[i])
        }
    }
    val soldAds=ArrayList<EachAdResponse>()
    for (i in 0 until userads.size){
        if (userads[i].item?.sold=="true"){
            soldAds.add(userads[i])
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(background)) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .padding(start = 28.dp)
                .clip(CircleShape)
                .clickable {
                    navController.popBackStack()
                }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
            }
            Box(
                modifier = Modifier
                    .padding(start = 90.dp)
                    .clip(CircleShape), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "My Listings",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "All Ads", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(){
                items(1){
                    for (i in 0 until userads.size){
                        eachAd(item = userads[i])
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Active Ads", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(){
                items(1){
                    for (i in 0 until activeAds.size){
                        eachAd(item = activeAds[i])
                    }

                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Sold Ads", color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            LazyRow(){
                items(1){
                    for (i in 0 until soldAds.size){
                        eachAd(item = soldAds[i])
                    }
                }
            }
        }
    }
}
@Composable
fun help(navController: NavController){
    val email = "buxx.app@gmail.com"
    val context=LocalContext.current
    Scaffold(modifier = Modifier.background(Color.White),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Need help?")
            }
                context.startActivity(intent) },containerColor = main) {
                Text(text = "Need help?", color = betterWhite)
            }
        }
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(background)) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth()) {
                Box(modifier = Modifier
                    .padding(start = 28.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.popBackStack()
                    }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                }
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Help",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(Modifier.padding(16.dp)){
                items(1){
                    Text(text = "Terms of Use & Privacy Policy\n" +
                            "\n" +
                            "Last updated September 09, 2023\n" +
                            "\n" +
                            "AGREEMENT TO OUR LEGAL TERMS\n" +
                            "\n" +
                            "We are Buxx MarketPlace ('Company', 'we', 'us', or 'our').\n" +
                            "\n" +
                            "We operate the mobile application Buxx (the 'App'), as well as any other related products and services that refer or link to these legal terms (the 'Legal Terms') (collectively, the 'Services').\n" +
                            "\n" +
                            "You can contact us by email at buxx.app@gmail.com.\n" +
                            "\n" +
                            "These Legal Terms constitute a legally binding agreement made between you, whether personally or on behalf of an entity ('you'), and Buxx MarketPlace , concerning your access to and use of the Services. You agree that by accessing the Services, you have read, understood, and agreed to be bound by all of these Legal Terms. IF YOU DO NOT AGREE WITH ALL OF THESE LEGAL TERMS, THEN YOU ARE EXPRESSLY PROHIBITED FROM USING THE SERVICES AND YOU MUST DISCONTINUE USE IMMEDIATELY.\n" +
                            "\n" +
                            "Supplemental terms and conditions or documents that may be posted on the Services from time to time are hereby expressly incorporated herein by reference. We reserve the right, in our sole discretion, to make changes or modifications to these Legal Terms at any time and for any reason. We will alert you about any changes by updating the 'Last updated' date of these Legal Terms, and you waive any right to receive specific notice of each such change. It is your responsibility to periodically review these Legal Terms to stay informed of updates. You will be subject to, and will be deemed to have been made aware of and to have accepted, the changes in any revised Legal Terms by your continued use of the Services after the date such revised Legal Terms are posted.\n" +
                            "\n" +
                            "All users who are minors in the jurisdiction in which they reside (generally under the age of 18) must have the permission of, and be directly supervised by, their parent or guardian to use the Services. If you are a minor, you must have your parent or guardian read and agree to these Legal Terms prior to you using the Services.\n" +
                            "\n" +
                            "We recommend that you print a copy of these Legal Terms for your records.\n" +
                            "\n" +
                            "1. OUR SERVICES\n" +
                            "\n" +
                            "The information provided when using the Services is not intended for distribution to or use by any person or entity in any jurisdiction or country where such distribution or use would be contrary to law or regulation or which would subject us to any registration requirement within such jurisdiction or country. Accordingly, those persons who choose to access the Services from other locations do so on their own initiative and are solely responsible for compliance with local laws, if and to the extent local laws are applicable.\n" +
                            "\n" +
                            "2. INTELLECTUAL PROPERTY RIGHTS\n" +
                            "\n" +
                            "Our intellectual property\n" +
                            "\n" +
                            "We are the owner or the licensee of all intellectual property rights in our Services, including all source code, databases, functionality, software, website designs, audio, video, text, photographs, and graphics in the Services (collectively, the 'Content'), as well as the trademarks, service marks, and logos contained therein (the 'Marks').\n" +
                            "\n" +
                            "Our Content and Marks are protected by copyright and trademark laws (and various other intellectual property rights and unfair competition laws) and treaties in the United States and around the world.\n" +
                            "\n" +
                            "The Content and Marks are provided in or through the Services 'AS IS' for your personal, non-commercial use only.\n" +
                            "\n" +
                            "Your use of our Services\n" +
                            "\n" +
                            "Subject to your compliance with these Legal Terms, including the 'PROHIBITED ACTIVITIES' section below, we grant you a non-exclusive, non-transferable, revocable licence to:\n" +
                            "access the Services; and\n" +
                            "download or print a copy of any portion of the Content to which you have properly gained access.\n" +
                            "solely for your personal, non-commercial use.\n" +
                            "\n" +
                            "Except as set out in this section or elsewhere in our Legal Terms, no part of the Services and no Content or Marks may be copied, reproduced, aggregated, republished, uploaded, posted, publicly displayed, encoded, translated, transmitted, distributed, sold, licensed, or otherwise exploited for any commercial purpose whatsoever, without our express prior written permission.\n" +
                            "\n" +
                            "If you wish to make any use of the Services, Content, or Marks other than as set out in this section or elsewhere in our Legal Terms, please address your request to: buxx.app@gmail.com. If we ever grant you the permission to post, reproduce, or publicly display any part of our Services or Content, you must identify us as the owners or licensors of the Services, Content, or Marks and ensure that any copyright or proprietary notice appears or is visible on posting, reproducing, or displaying our Content.\n" +
                            "\n" +
                            "We reserve all rights not expressly granted to you in and to the Services, Content, and Marks.\n" +
                            "\n" +
                            "Any breach of these Intellectual Property Rights will constitute a material breach of our Legal Terms and your right to use our Services will terminate immediately.\n" +
                            "\n" +
                            "Your submissions and contributions\n" +
                            "\n" +
                            "Please review this section and the 'PROHIBITED ACTIVITIES' section carefully prior to using our Services to understand the (a) rights you give us and (b) obligations you have when you post or upload any content through the Services.\n" +
                            "\n" +
                            "Submissions: By directly sending us any question, comment, suggestion, idea, feedback, or other information about the Services ('Submissions'), you agree to assign to us all intellectual property rights in such Submission. You agree that we shall own this Submission and be entitled to its unrestricted use and dissemination for any lawful purpose, commercial or otherwise, without acknowledgment or compensation to you.\n" +
                            "\n" +
                            "Contributions: The Services may invite you to chat, contribute to, or participate in blogs, message boards, online forums, and other functionality during which you may create, submit, post, display, transmit, publish, distribute, or broadcast content and materials to us or through the Services, including but not limited to text, writings, video, audio, photographs, music, graphics, comments, reviews, rating suggestions, personal information, or other material ('Contributions'). Any Submission that is publicly posted shall also be treated as a Contribution.\n" +
                            "\n" +
                            "You understand that Contributions may be viewable by other users of the Services.\n" +
                            "\n" +
                            "When you post Contributions, you grant us a licence (including use of your name, trademarks, and logos): By posting any Contributions, you grant us an unrestricted, unlimited, irrevocable, perpetual, non-exclusive, transferable, royalty-free, fully-paid, worldwide right, and licence to: use, copy, reproduce, distribute, sell, resell, publish, broadcast, retitle, store, publicly perform, publicly display, reformat, translate, excerpt (in whole or in part), and exploit your Contributions (including, without limitation, your image, name, and voice) for any purpose, commercial, advertising, or otherwise, to prepare derivative works of, or incorporate into other works, your Contributions, and to sublicence the licences granted in this section. Our use and distribution may occur in any media formats and through any media channels.\n" +
                            "\n" +
                            "This licence includes our use of your name, company name, and franchise name, as applicable, and any of the trademarks, service marks, trade names, logos, and personal and commercial images you provide.\n" +
                            "\n" +
                            "You are responsible for what you post or upload: By sending us Submissions and/or posting Contributions through any part of the Services or making Contributions accessible through the Services by linking your account through the Services to any of your social networking accounts, you:\n" +
                            "confirm that you have read and agree with our 'PROHIBITED ACTIVITIES' and will not post, send, publish, upload, or transmit through the Services any Submission nor post any Contribution that is illegal, harassing, hateful, harmful, defamatory, obscene, bullying, abusive, discriminatory, threatening to any person or group, sexually explicit, false, inaccurate, deceitful, or misleading;\n" +
                            "to the extent permissible by applicable law, waive any and all moral rights to any such Submission and/or Contribution;\n" +
                            "warrant that any such Submission and/or Contributions are original to you or that you have the necessary rights and licences to submit such Submissions and/or Contributions and that you have full authority to grant us the above-mentioned rights in relation to your Submissions and/or Contributions; and\n" +
                            "warrant and represent that your Submissions and/or Contributions do not constitute confidential information.\n" +
                            "You are solely responsible for your Submissions and/or Contributions and you expressly agree to reimburse us for any and all losses that we may suffer because of your breach of (a) this section, (b) any third party’s intellectual property rights, or (c) applicable law.\n" +
                            "\n" +
                            "We may remove or edit your Content: Although we have no obligation to monitor any Contributions, we shall have the right to remove or edit any Contributions at any time without notice if in our reasonable opinion we consider such Contributions harmful or in breach of these Legal Terms. If we remove or edit any such Contributions, we may also suspend or disable your account and report you to the authorities.\n" +
                            "\n" +
                            "3. USER REPRESENTATIONS\n" +
                            "\n" +
                            "By using the Services, you represent and warrant that: (1) all registration information you submit will be true, accurate, current, and complete; (2) you will maintain the accuracy of such information and promptly update such registration information as necessary; (3) you have the legal capacity and you agree to comply with these Legal Terms; (4) you are not a minor in the jurisdiction in which you reside, or if a minor, you have received parental permission to use the Services; (5) you will not access the Services through automated or non-human means, whether through a bot, script or otherwise; (6) you will not use the Services for any illegal or unauthorised purpose; and (7) your use of the Services will not violate any applicable law or regulation.\n" +
                            "\n" +
                            "If you provide any information that is untrue, inaccurate, not current, or incomplete, we have the right to suspend or terminate your account and refuse any and all current or future use of the Services (or any portion thereof).\n" +
                            "\n" +
                            "4. USER REGISTRATION\n" +
                            "\n" +
                            "You may be required to register to use the Services. You agree to keep your password confidential and will be responsible for all use of your account and password. We reserve the right to remove, reclaim, or change a username you select if we determine, in our sole discretion, that such username is inappropriate, obscene, or otherwise objectionable.\n" +
                            "\n" +
                            "5. PRODUCTS\n" +
                            "\n" +
                            "We make every effort to display as accurately as possible the colours, features, specifications, and details of the products available on the Services. However, we do not guarantee that the colours, features, specifications, and details of the products will be accurate, complete, reliable, current, or free of other errors, and your electronic display may not accurately reflect the actual colours and details of the products. All products are subject to availability, and we cannot guarantee that items will be in stock. We reserve the right to discontinue any products at any time for any reason. Prices for all products are subject to change.\n" +
                            "\n" +
                            "6. PURCHASES AND PAYMENT\n" +
                            "\n" +
                            "We accept the following forms of payment:\n" +
                            "\n" +
                            "\n" +
                            "You agree to provide current, complete, and accurate purchase and account information for all purchases made via the Services. You further agree to promptly update account and payment information, including email address, payment method, and payment card expiration date, so that we can complete your transactions and contact you as needed. Sales tax will be added to the price of purchases as deemed required by us. We may change prices at any time. All payments shall be in __________.\n" +
                            "\n" +
                            "You agree to pay all charges at the prices then in effect for your purchases and any applicable shipping fees, and you authorise us to charge your chosen payment provider for any such amounts upon placing your order. We reserve the right to correct any errors or mistakes in pricing, even if we have already requested or received payment.\n" +
                            "\n" +
                            "We reserve the right to refuse any order placed through the Services. We may, in our sole discretion, limit or cancel quantities purchased per person, per household, or per order. These restrictions may include orders placed by or under the same customer account, the same payment method, and/or orders that use the same billing or shipping address. We reserve the right to limit or prohibit orders that, in our sole judgement, appear to be placed by dealers, resellers, or distributors.\n" +
                            "\n" +
                            "7. RETURN POLICY\n" +
                            "\n" +
                            "All sales are final and no refund will be issued.\n" +
                            "\n" +
                            "8. PROHIBITED ACTIVITIES\n" +
                            "\n" +
                            "You may not access or use the Services for any purpose other than that for which we make the Services available. The Services may not be used in connection with any commercial endeavours except those that are specifically endorsed or approved by us.\n" +
                            "\n" +
                            "As a user of the Services, you agree not to:\n" +
                            "Systematically retrieve data or other content from the Services to create or compile, directly or indirectly, a collection, compilation, database, or directory without written permission from us.\n" +
                            "Trick, defraud, or mislead us and other users, especially in any attempt to learn sensitive account information such as user passwords.\n" +
                            "Circumvent, disable, or otherwise interfere with security-related features of the Services, including features that prevent or restrict the use or copying of any Content or enforce limitations on the use of the Services and/or the Content contained therein.\n" +
                            "Disparage, tarnish, or otherwise harm, in our opinion, us and/or the Services.\n" +
                            "Use any information obtained from the Services in order to harass, abuse, or harm another person.\n" +
                            "Make improper use of our support services or submit false reports of abuse or misconduct.\n" +
                            "Use the Services in a manner inconsistent with any applicable laws or regulations.\n" +
                            "Engage in unauthorised framing of or linking to the Services.\n" +
                            "Upload or transmit (or attempt to upload or to transmit) viruses, Trojan horses, or other material, including excessive use of capital letters and spamming (continuous posting of repetitive text), that interferes with any party’s uninterrupted use and enjoyment of the Services or modifies, impairs, disrupts, alters, or interferes with the use, features, functions, operation, or maintenance of the Services.\n" +
                            "Engage in any automated use of the system, such as using scripts to send comments or messages, or using any data mining, robots, or similar data gathering and extraction tools.\n" +
                            "Delete the copyright or other proprietary rights notice from any Content.\n" +
                            "Attempt to impersonate another user or person or use the username of another user.\n" +
                            "Upload or transmit (or attempt to upload or to transmit) any material that acts as a passive or active information collection or transmission mechanism, including without limitation, clear graphics interchange formats ('gifs'), 1×1 pixels, web bugs, cookies, or other similar devices (sometimes referred to as 'spyware' or 'passive collection mechanisms' or 'pcms').\n" +
                            "Interfere with, disrupt, or create an undue burden on the Services or the networks or services connected to the Services.\n" +
                            "Harass, annoy, intimidate, or threaten any of our employees or agents engaged in providing any portion of the Services to you.\n" +
                            "Attempt to bypass any measures of the Services designed to prevent or restrict access to the Services, or any portion of the Services.\n" +
                            "Copy or adapt the Services' software, including but not limited to Flash, PHP, HTML, JavaScript, or other code.\n" +
                            "Except as permitted by applicable law, decipher, decompile, disassemble, or reverse engineer any of the software comprising or in any way making up a part of the Services.\n" +
                            "Except as may be the result of standard search engine or Internet browser usage, use, launch, develop, or distribute any automated system, including without limitation, any spider, robot, cheat utility, scraper, or offline reader that accesses the Services, or use or launch any unauthorised script or other software.\n" +
                            "Use a buying agent or purchasing agent to make purchases on the Services.\n" +
                            "Make any unauthorised use of the Services, including collecting usernames and/or email addresses of users by electronic or other means for the purpose of sending unsolicited email, or creating user accounts by automated means or under false pretences.\n" +
                            "Use the Services as part of any effort to compete with us or otherwise use the Services and/or the Content for any revenue-generating endeavour or commercial enterprise.\n" +
                            "Sell or otherwise transfer your profile.\n" +
                            "\n" +
                            "9. USER GENERATED CONTRIBUTIONS\n" +
                            "\n" +
                            "The Services may invite you to chat, contribute to, or participate in blogs, message boards, online forums, and other functionality, and may provide you with the opportunity to create, submit, post, display, transmit, perform, publish, distribute, or broadcast content and materials to us or on the Services, including but not limited to text, writings, video, audio, photographs, graphics, comments, suggestions, or personal information or other material (collectively, 'Contributions'). Contributions may be viewable by other users of the Services and through third-party websites. As such, any Contributions you transmit may be treated as non-confidential and non-proprietary. When you create or make available any Contributions, you thereby represent and warrant that:\n" +
                            "The creation, distribution, transmission, public display, or performance, and the accessing, downloading, or copying of your Contributions do not and will not infringe the proprietary rights, including but not limited to the copyright, patent, trademark, trade secret, or moral rights of any third party.\n" +
                            "You are the creator and owner of or have the necessary licences, rights, consents, releases, and permissions to use and to authorise us, the Services, and other users of the Services to use your Contributions in any manner contemplated by the Services and these Legal Terms.\n" +
                            "You have the written consent, release, and/or permission of each and every identifiable individual person in your Contributions to use the name or likeness of each and every such identifiable individual person to enable inclusion and use of your Contributions in any manner contemplated by the Services and these Legal Terms.\n" +
                            "Your Contributions are not false, inaccurate, or misleading.\n" +
                            "Your Contributions are not unsolicited or unauthorised advertising, promotional materials, pyramid schemes, chain letters, spam, mass mailings, or other forms of solicitation.\n" +
                            "Your Contributions are not obscene, lewd, lascivious, filthy, violent, harassing, libellous, slanderous, or otherwise objectionable (as determined by us).\n" +
                            "Your Contributions do not ridicule, mock, disparage, intimidate, or abuse anyone.\n" +
                            "Your Contributions are not used to harass or threaten (in the legal sense of those terms) any other person and to promote violence against a specific person or class of people.\n" +
                            "Your Contributions do not violate any applicable law, regulation, or rule.\n" +
                            "Your Contributions do not violate the privacy or publicity rights of any third party.\n" +
                            "Your Contributions do not violate any applicable law concerning child pornography, or otherwise intended to protect the health or well-being of minors.\n" +
                            "Your Contributions do not include any offensive comments that are connected to race, national origin, gender, sexual preference, or physical handicap.\n" +
                            "Your Contributions do not otherwise violate, or link to material that violates, any provision of these Legal Terms, or any applicable law or regulation.\n" +
                            "Any use of the Services in violation of the foregoing violates these Legal Terms and may result in, among other things, termination or suspension of your rights to use the Services.\n" +
                            "\n" +
                            "10. CONTRIBUTION LICENCE\n" +
                            "\n" +
                            "By posting your Contributions to any part of the Services or making Contributions accessible to the Services by linking your account from the Services to any of your social networking accounts, you automatically grant, and you represent and warrant that you have the right to grant, to us an unrestricted, unlimited, irrevocable, perpetual, non-exclusive, transferable, royalty-free, fully-paid, worldwide right, and licence to host, use, copy, reproduce, disclose, sell, resell, publish, broadcast, retitle, archive, store, cache, publicly perform, publicly display, reformat, translate, transmit, excerpt (in whole or in part), and distribute such Contributions (including, without limitation, your image and voice) for any purpose, commercial, advertising, or otherwise, and to prepare derivative works of, or incorporate into other works, such Contributions, and grant and authorise sublicences of the foregoing. The use and distribution may occur in any media formats and through any media channels.\n" +
                            "\n" +
                            "This licence will apply to any form, media, or technology now known or hereafter developed, and includes our use of your name, company name, and franchise name, as applicable, and any of the trademarks, service marks, trade names, logos, and personal and commercial images you provide. You waive all moral rights in your Contributions, and you warrant that moral rights have not otherwise been asserted in your Contributions.\n" +
                            "\n" +
                            "We do not assert any ownership over your Contributions. You retain full ownership of all of your Contributions and any intellectual property rights or other proprietary rights associated with your Contributions. We are not liable for any statements or representations in your Contributions provided by you in any area on the Services. You are solely responsible for your Contributions to the Services and you expressly agree to exonerate us from any and all responsibility and to refrain from any legal action against us regarding your Contributions.\n" +
                            "\n" +
                            "We have the right, in our sole and absolute discretion, (1) to edit, redact, or otherwise change any Contributions; (2) to re-categorise any Contributions to place them in more appropriate locations on the Services; and (3) to pre-screen or delete any Contributions at any time and for any reason, without notice. We have no obligation to monitor your Contributions.\n" +
                            "\n" +
                            "11. MOBILE APPLICATION LICENCE\n" +
                            "\n" +
                            "Use Licence\n" +
                            "\n" +
                            "If you access the Services via the App, then we grant you a revocable, non-exclusive, non-transferable, limited right to install and use the App on wireless electronic devices owned or controlled by you, and to access and use the App on such devices strictly in accordance with the terms and conditions of this mobile application licence contained in these Legal Terms. You shall not: (1) except as permitted by applicable law, decompile, reverse engineer, disassemble, attempt to derive the source code of, or decrypt the App; (2) make any modification, adaptation, improvement, enhancement, translation, or derivative work from the App; (3) violate any applicable laws, rules, or regulations in connection with your access or use of the App; (4) remove, alter, or obscure any proprietary notice (including any notice of copyright or trademark) posted by us or the licensors of the App; (5) use the App for any revenue-generating endeavour, commercial enterprise, or other purpose for which it is not designed or intended; (6) make the App available over a network or other environment permitting access or use by multiple devices or users at the same time; (7) use the App for creating a product, service, or software that is, directly or indirectly, competitive with or in any way a substitute for the App; (8) use the App to send automated queries to any website or to send any unsolicited commercial email; or (9) use any proprietary information or any of our interfaces or our other intellectual property in the design, development, manufacture, licensing, or distribution of any applications, accessories, or devices for use with the App.\n" +
                            "\n" +
                            "Apple and Android Devices\n" +
                            "\n" +
                            "The following terms apply when you use the App obtained from either the Apple Store or Google Play (each an 'App Distributor') to access the Services: (1) the licence granted to you for our App is limited to a non-transferable licence to use the application on a device that utilises the Apple iOS or Android operating systems, as applicable, and in accordance with the usage rules set forth in the applicable App Distributor’s terms of service; (2) we are responsible for providing any maintenance and support services with respect to the App as specified in the terms and conditions of this mobile application licence contained in these Legal Terms or as otherwise required under applicable law, and you acknowledge that each App Distributor has no obligation whatsoever to furnish any maintenance and support services with respect to the App; (3) in the event of any failure of the App to conform to any applicable warranty, you may notify the applicable App Distributor, and the App Distributor, in accordance with its terms and policies, may refund the purchase price, if any, paid for the App, and to the maximum extent permitted by applicable law, the App Distributor will have no other warranty obligation whatsoever with respect to the App; (4) you represent and warrant that (i) you are not located in a country that is subject to a US government embargo, or that has been designated by the US government as a 'terrorist supporting' country and (ii) you are not listed on any US government list of prohibited or restricted parties; (5) you must comply with applicable third-party terms of agreement when using the App, e.g. if you have a VoIP application, then you must not be in violation of their wireless data service agreement when using the App; and (6) you acknowledge and agree that the App Distributors are third-party beneficiaries of the terms and conditions in this mobile application licence contained in these Legal Terms, and that each App Distributor will have the right (and will be deemed to have accepted the right) to enforce the terms and conditions in this mobile application licence contained in these Legal Terms against you as a third-party beneficiary thereof.\n" +
                            "\n" +
                            "12. SOCIAL MEDIA\n" +
                            "\n" +
                            "As part of the functionality of the Services, you may link your account with online accounts you have with third-party service providers (each such account, a 'Third-Party Account') by either: (1) providing your Third-Party Account login information through the Services; or (2) allowing us to access your Third-Party Account, as is permitted under the applicable terms and conditions that govern your use of each Third-Party Account. You represent and warrant that you are entitled to disclose your Third-Party Account login information to us and/or grant us access to your Third-Party Account, without breach by you of any of the terms and conditions that govern your use of the applicable Third-Party Account, and without obligating us to pay any fees or making us subject to any usage limitations imposed by the third-party service provider of the Third-Party Account. By granting us access to any Third-Party Accounts, you understand that (1) we may access, make available, and store (if applicable) any content that you have provided to and stored in your Third-Party Account (the 'Social Network Content') so that it is available on and through the Services via your account, including without limitation any friend lists and (2) we may submit to and receive from your Third-Party Account additional information to the extent you are notified when you link your account with the Third-Party Account. Depending on the Third-Party Accounts you choose and subject to the privacy settings that you have set in such Third-Party Accounts, personally identifiable information that you post to your Third-Party Accounts may be available on and through your account on the Services. Please note that if a Third-Party Account or associated service becomes unavailable or our access to such Third-Party Account is terminated by the third-party service provider, then Social Network Content may no longer be available on and through the Services. You will have the ability to disable the connection between your account on the Services and your Third-Party Accounts at any time. PLEASE NOTE THAT YOUR RELATIONSHIP WITH THE THIRD-PARTY SERVICE PROVIDERS ASSOCIATED WITH YOUR THIRD-PARTY ACCOUNTS IS GOVERNED SOLELY BY YOUR AGREEMENT(S) WITH SUCH THIRD-PARTY SERVICE PROVIDERS. We make no effort to review any Social Network Content for any purpose, including but not limited to, for accuracy, legality, or non-infringement, and we are not responsible for any Social Network Content. You acknowledge and agree that we may access your email address book associated with a Third-Party Account and your contacts list stored on your mobile device or tablet computer solely for purposes of identifying and informing you of those contacts who have also registered to use the Services. You can deactivate the connection between the Services and your Third-Party Account by contacting us using the contact information below or through your account settings (if applicable). We will attempt to delete any information stored on our servers that was obtained through such Third-Party Account, except the username and profile picture that become associated with your account.\n" +
                            "\n" +
                            "13. SERVICES MANAGEMENT\n" +
                            "\n" +
                            "We reserve the right, but not the obligation, to: (1) monitor the Services for violations of these Legal Terms; (2) take appropriate legal action against anyone who, in our sole discretion, violates the law or these Legal Terms, including without limitation, reporting such user to law enforcement authorities; (3) in our sole discretion and without limitation, refuse, restrict access to, limit the availability of, or disable (to the extent technologically feasible) any of your Contributions or any portion thereof; (4) in our sole discretion and without limitation, notice, or liability, to remove from the Services or otherwise disable all files and content that are excessive in size or are in any way burdensome to our systems; and (5) otherwise manage the Services in a manner designed to protect our rights and property and to facilitate the proper functioning of the Services.\n" +
                            "\n" +
                            "14. PRIVACY POLICY\n" +
                            "\n" +
                            "We care about data privacy and security. By using the Services, you agree to be bound by our Privacy Policy posted on the Services, which is incorporated into these Legal Terms. Please be advised the Services are hosted in India. If you access the Services from any other region of the world with laws or other requirements governing personal data collection, use, or disclosure that differ from applicable laws in India, then through your continued use of the Services, you are transferring your data to India, and you expressly consent to have your data transferred to and processed in India.\n" +
                            "\n" +
                            "15. TERM AND TERMINATION\n" +
                            "\n" +
                            "These Legal Terms shall remain in full force and effect while you use the Services. WITHOUT LIMITING ANY OTHER PROVISION OF THESE LEGAL TERMS, WE RESERVE THE RIGHT TO, IN OUR SOLE DISCRETION AND WITHOUT NOTICE OR LIABILITY, DENY ACCESS TO AND USE OF THE SERVICES (INCLUDING BLOCKING CERTAIN IP ADDRESSES), TO ANY PERSON FOR ANY REASON OR FOR NO REASON, INCLUDING WITHOUT LIMITATION FOR BREACH OF ANY REPRESENTATION, WARRANTY, OR COVENANT CONTAINED IN THESE LEGAL TERMS OR OF ANY APPLICABLE LAW OR REGULATION. WE MAY TERMINATE YOUR USE OR PARTICIPATION IN THE SERVICES OR DELETE YOUR ACCOUNT AND ANY CONTENT OR INFORMATION THAT YOU POSTED AT ANY TIME, WITHOUT WARNING, IN OUR SOLE DISCRETION.\n" +
                            "\n" +
                            "If we terminate or suspend your account for any reason, you are prohibited from registering and creating a new account under your name, a fake or borrowed name, or the name of any third party, even if you may be acting on behalf of the third party. In addition to terminating or suspending your account, we reserve the right to take appropriate legal action, including without limitation pursuing civil, criminal, and injunctive redress.\n" +
                            "\n" +
                            "16. MODIFICATIONS AND INTERRUPTIONS\n" +
                            "\n" +
                            "We reserve the right to change, modify, or remove the contents of the Services at any time or for any reason at our sole discretion without notice. However, we have no obligation to update any information on our Services. We also reserve the right to modify or discontinue all or part of the Services without notice at any time. We will not be liable to you or any third party for any modification, price change, suspension, or discontinuance of the Services.\n" +
                            "\n" +
                            "We cannot guarantee the Services will be available at all times. We may experience hardware, software, or other problems or need to perform maintenance related to the Services, resulting in interruptions, delays, or errors. We reserve the right to change, revise, update, suspend, discontinue, or otherwise modify the Services at any time or for any reason without notice to you. You agree that we have no liability whatsoever for any loss, damage, or inconvenience caused by your inability to access or use the Services during any downtime or discontinuance of the Services. Nothing in these Legal Terms will be construed to obligate us to maintain and support the Services or to supply any corrections, updates, or releases in connection therewith.\n" +
                            "\n" +
                            "17. GOVERNING LAW\n" +
                            "\n" +
                            "These Legal Terms shall be governed by and defined following the laws of India. Buxx MarketPlace and yourself irrevocably consent that the courts of India shall have exclusive jurisdiction to resolve any dispute which may arise in connection with these Legal Terms.\n" +
                            "\n" +
                            "18. DISPUTE RESOLUTION\n" +
                            "\n" +
                            "Informal Negotiations\n" +
                            "\n" +
                            "To expedite resolution and control the cost of any dispute, controversy, or claim related to these Legal Terms (each a 'Dispute' and collectively, the 'Disputes') brought by either you or us (individually, a 'Party' and collectively, the 'Parties'), the Parties agree to first attempt to negotiate any Dispute (except those Disputes expressly provided below) informally for at least __________ days before initiating arbitration. Such informal negotiations commence upon written notice from one Party to the other Party.\n" +
                            "\n" +
                            "Binding Arbitration\n" +
                            "\n" +
                            "Any dispute arising out of or in connection with these Legal Terms, including any question regarding its existence, validity, or termination, shall be referred to and finally resolved by the International Commercial Arbitration Court under the European Arbitration Chamber (Belgium, Brussels, Avenue Louise, 146) according to the Rules of this ICAC, which, as a result of referring to it, is considered as the part of this clause. The number of arbitrators shall be __________. The seat, or legal place, or arbitration shall be __________. The language of the proceedings shall be __________. The governing law of these Legal Terms shall be substantive law of __________.\n" +
                            "\n" +
                            "Restrictions\n" +
                            "\n" +
                            "The Parties agree that any arbitration shall be limited to the Dispute between the Parties individually. To the full extent permitted by law, (a) no arbitration shall be joined with any other proceeding; (b) there is no right or authority for any Dispute to be arbitrated on a class-action basis or to utilise class action procedures; and (c) there is no right or authority for any Dispute to be brought in a purported representative capacity on behalf of the general public or any other persons.\n" +
                            "\n" +
                            "Exceptions to Informal Negotiations and Arbitration\n" +
                            "\n" +
                            "The Parties agree that the following Disputes are not subject to the above provisions concerning informal negotiations binding arbitration: (a) any Disputes seeking to enforce or protect, or concerning the validity of, any of the intellectual property rights of a Party; (b) any Dispute related to, or arising from, allegations of theft, piracy, invasion of privacy, or unauthorised use; and (c) any claim for injunctive relief. If this provision is found to be illegal or unenforceable, then neither Party will elect to arbitrate any Dispute falling within that portion of this provision found to be illegal or unenforceable and such Dispute shall be decided by a court of competent jurisdiction within the courts listed for jurisdiction above, and the Parties agree to submit to the personal jurisdiction of that court.\n" +
                            "\n" +
                            "19. CORRECTIONS\n" +
                            "\n" +
                            "There may be information on the Services that contains typographical errors, inaccuracies, or omissions, including descriptions, pricing, availability, and various other information. We reserve the right to correct any errors, inaccuracies, or omissions and to change or update the information on the Services at any time, without prior notice.\n" +
                            "\n" +
                            "20. DISCLAIMER\n" +
                            "\n" +
                            "THE SERVICES ARE PROVIDED ON AN AS-IS AND AS-AVAILABLE BASIS. YOU AGREE THAT YOUR USE OF THE SERVICES WILL BE AT YOUR SOLE RISK. TO THE FULLEST EXTENT PERMITTED BY LAW, WE DISCLAIM ALL WARRANTIES, EXPRESS OR IMPLIED, IN CONNECTION WITH THE SERVICES AND YOUR USE THEREOF, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. WE MAKE NO WARRANTIES OR REPRESENTATIONS ABOUT THE ACCURACY OR COMPLETENESS OF THE SERVICES' CONTENT OR THE CONTENT OF ANY WEBSITES OR MOBILE APPLICATIONS LINKED TO THE SERVICES AND WE WILL ASSUME NO LIABILITY OR RESPONSIBILITY FOR ANY (1) ERRORS, MISTAKES, OR INACCURACIES OF CONTENT AND MATERIALS, (2) PERSONAL INJURY OR PROPERTY DAMAGE, OF ANY NATURE WHATSOEVER, RESULTING FROM YOUR ACCESS TO AND USE OF THE SERVICES, (3) ANY UNAUTHORISED ACCESS TO OR USE OF OUR SECURE SERVERS AND/OR ANY AND ALL PERSONAL INFORMATION AND/OR FINANCIAL INFORMATION STORED THEREIN, (4) ANY INTERRUPTION OR CESSATION OF TRANSMISSION TO OR FROM THE SERVICES, (5) ANY BUGS, VIRUSES, TROJAN HORSES, OR THE LIKE WHICH MAY BE TRANSMITTED TO OR THROUGH THE SERVICES BY ANY THIRD PARTY, AND/OR (6) ANY ERRORS OR OMISSIONS IN ANY CONTENT AND MATERIALS OR FOR ANY LOSS OR DAMAGE OF ANY KIND INCURRED AS A RESULT OF THE USE OF ANY CONTENT POSTED, TRANSMITTED, OR OTHERWISE MADE AVAILABLE VIA THE SERVICES. WE DO NOT WARRANT, ENDORSE, GUARANTEE, OR ASSUME RESPONSIBILITY FOR ANY PRODUCT OR SERVICE ADVERTISED OR OFFERED BY A THIRD PARTY THROUGH THE SERVICES, ANY HYPERLINKED WEBSITE, OR ANY WEBSITE OR MOBILE APPLICATION FEATURED IN ANY BANNER OR OTHER ADVERTISING, AND WE WILL NOT BE A PARTY TO OR IN ANY WAY BE RESPONSIBLE FOR MONITORING ANY TRANSACTION BETWEEN YOU AND ANY THIRD-PARTY PROVIDERS OF PRODUCTS OR SERVICES. AS WITH THE PURCHASE OF A PRODUCT OR SERVICE THROUGH ANY MEDIUM OR IN ANY ENVIRONMENT, YOU SHOULD USE YOUR BEST JUDGEMENT AND EXERCISE CAUTION WHERE APPROPRIATE.\n" +
                            "\n" +
                            "21. LIMITATIONS OF LIABILITY\n" +
                            "\n" +
                            "IN NO EVENT WILL WE OR OUR DIRECTORS, EMPLOYEES, OR AGENTS BE LIABLE TO YOU OR ANY THIRD PARTY FOR ANY DIRECT, INDIRECT, CONSEQUENTIAL, EXEMPLARY, INCIDENTAL, SPECIAL, OR PUNITIVE DAMAGES, INCLUDING LOST PROFIT, LOST REVENUE, LOSS OF DATA, OR OTHER DAMAGES ARISING FROM YOUR USE OF THE SERVICES, EVEN IF WE HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.\n" +
                            "\n" +
                            "22. INDEMNIFICATION\n" +
                            "\n" +
                            "You agree to defend, indemnify, and hold us harmless, including our subsidiaries, affiliates, and all of our respective officers, agents, partners, and employees, from and against any loss, damage, liability, claim, or demand, including reasonable attorneys’ fees and expenses, made by any third party due to or arising out of: (1) your Contributions; (2) use of the Services; (3) breach of these Legal Terms; (4) any breach of your representations and warranties set forth in these Legal Terms; (5) your violation of the rights of a third party, including but not limited to intellectual property rights; or (6) any overt harmful act toward any other user of the Services with whom you connected via the Services. Notwithstanding the foregoing, we reserve the right, at your expense, to assume the exclusive defence and control of any matter for which you are required to indemnify us, and you agree to cooperate, at your expense, with our defence of such claims. We will use reasonable efforts to notify you of any such claim, action, or proceeding which is subject to this indemnification upon becoming aware of it.\n" +
                            "\n" +
                            "23. USER DATA\n" +
                            "\n" +
                            "We will maintain certain data that you transmit to the Services for the purpose of managing the performance of the Services, as well as data relating to your use of the Services. Although we perform regular routine backups of data, you are solely responsible for all data that you transmit or that relates to any activity you have undertaken using the Services. You agree that we shall have no liability to you for any loss or corruption of any such data, and you hereby waive any right of action against us arising from any such loss or corruption of such data.\n" +
                            "\n" +
                            "24. ELECTRONIC COMMUNICATIONS, TRANSACTIONS, AND SIGNATURES\n" +
                            "\n" +
                            "Visiting the Services, sending us emails, and completing online forms constitute electronic communications. You consent to receive electronic communications, and you agree that all agreements, notices, disclosures, and other communications we provide to you electronically, via email and on the Services, satisfy any legal requirement that such communication be in writing. YOU HEREBY AGREE TO THE USE OF ELECTRONIC SIGNATURES, CONTRACTS, ORDERS, AND OTHER RECORDS, AND TO ELECTRONIC DELIVERY OF NOTICES, POLICIES, AND RECORDS OF TRANSACTIONS INITIATED OR COMPLETED BY US OR VIA THE SERVICES. You hereby waive any rights or requirements under any statutes, regulations, rules, ordinances, or other laws in any jurisdiction which require an original signature or delivery or retention of non-electronic records, or to payments or the granting of credits by any means other than electronic means.\n" +
                            "\n" +
                            "25. MISCELLANEOUS\n" +
                            "\n" +
                            "These Legal Terms and any policies or operating rules posted by us on the Services or in respect to the Services constitute the entire agreement and understanding between you and us. Our failure to exercise or enforce any right or provision of these Legal Terms shall not operate as a waiver of such right or provision. These Legal Terms operate to the fullest extent permissible by law. We may assign any or all of our rights and obligations to others at any time. We shall not be responsible or liable for any loss, damage, delay, or failure to act caused by any cause beyond our reasonable control. If any provision or part of a provision of these Legal Terms is determined to be unlawful, void, or unenforceable, that provision or part of the provision is deemed severable from these Legal Terms and does not affect the validity and enforceability of any remaining provisions. There is no joint venture, partnership, employment or agency relationship created between you and us as a result of these Legal Terms or use of the Services. You agree that these Legal Terms will not be construed against us by virtue of having drafted them. You hereby waive any and all defences you may have based on the electronic form of these Legal Terms and the lack of signing by the parties hereto to execute these Legal Terms.\n" +
                            "\n" +
                            "26. CONTACT US\n" +
                            "\n" +
                            "In order to resolve a complaint regarding the Services or to receive further information regarding use of the Services, please contact us at:\n" +
                            "\n" +
                            "Buxx MarketPlace\n" +
                            "__________\n" +
                            "buxx.app@gmail.com",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Justify,)
                }
            }
        }
    }

}