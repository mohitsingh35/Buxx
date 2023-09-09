package com.ncs.tradezy

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    if (filteredList?.isNotEmpty() == true) {
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
                googleAuthUiClient = googleAuthUiClient
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
                    .clickable { }
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
                    keyboardOptions = KeyboardOptions.Default.copy(
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
    googleAuthUiClient: GoogleAuthUIClient
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
                    keyboardOptions = KeyboardOptions.Default.copy(
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