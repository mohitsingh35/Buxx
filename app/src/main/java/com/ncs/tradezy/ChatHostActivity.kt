package com.ncs.tradezy

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.msgSelf
import com.ncs.tradezy.ui.theme.msgreceive
import com.ncs.tradezy.ui.theme.primaryTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatHostActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            primaryTheme {
                val navController= rememberNavController()
                val name = intent.getStringExtra("name")
                val id = intent.getStringExtra("id")
                val token = intent.getStringExtra("token")
                val dp = intent.getStringExtra("dp")
                NavigationChatHost(navController = navController,name =  name!! , id = id!!, fcmtoken = token!!, dp = dp!!)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun chatHost(name:String,id:String,fcmtoken:String,dp:String) {
    val viewModel: ChatViewModel = hiltViewModel()
    val viewModel2: ProfileActivityViewModel = hiltViewModel()
    val scope= rememberCoroutineScope()
    val context = LocalContext.current
    val lazyColumnListState = rememberLazyListState()

    var message = remember {
        mutableStateOf("")
    }
    val res = viewModel.res.value
    val res2 = viewModel2.res.value

    val senderid = FirebaseAuth.getInstance().currentUser?.uid
    val chatList= ArrayList<MessageResponse>()
    val currentUserData= ArrayList<RealTimeUserResponse>()

    for (i in 0 until res.item.size){
        if (res.item[i].item!!.receiverId.equals(id)&&res.item[i].item!!.senderId.equals(senderid)||
            res.item[i].item!!.receiverId.equals(senderid)&&res.item[i].item!!.senderId.equals(id)){
            chatList.add(res.item[i])
        }

    }
    for (i in 0 until res2.item.size){
        if (res2.item[i].item?.userId==senderid){
            currentUserData.add(res2.item[i])
        }
    }
    var showimageUpload by remember {
        mutableStateOf(false)
    }

    var imageUris by remember { mutableStateOf(emptyList<Uri>()) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {
                imageUris = it
                if (imageUris.isNotEmpty()) {
                    scope.launch(Dispatchers.Main) {
                        viewModel
                            .insertImages(
                                imageUris,
                                otherDetails = MessageResponse.MessageItems(
                                    senderId = senderid,
                                    receiverId = id,
                                    message = message.value,
                                    category = "Exchange",
                                    read = "false",
                                    time = System.currentTimeMillis()
                                )
                            )
                            .collect {
                                when (it) {
                                    is ResultState.Success -> {
                                        context.showMsg(
                                            msg = it.data
                                        )
                                        showimageUpload = false
                                    }

                                    is ResultState.Failure -> {
                                        context.showMsg(
                                            msg = it.msg.toString()
                                        )
                                    }

                                    ResultState.Loading -> {
                                        showimageUpload = true
                                    }
                                }
                            }
                        if (imageUris.isNotEmpty() && imageUris.size>1){
                            sendNotification(
                                PushNotification(
                                    NotificationData(
                                        currentUserData[0].item?.name!!,
                                        "Received ${imageUris.size} images"
                                    ), fcmtoken
                                )
                            )
                        }
                        else if(imageUris.isNotEmpty() && imageUris.size==1){
                            sendNotification(
                                PushNotification(
                                    NotificationData(
                                        currentUserData[0].item?.name!!,
                                        "Received an image"
                                    ), fcmtoken
                                )
                            )
                        }
                    }
                }
            }
        }
    var prevDate =""
    val lastIndex = chatList.lastIndex
    LaunchedEffect(lastIndex) {
        if (lastIndex >= 0) {
            scope.launch {
                lazyColumnListState.scrollToItem(lastIndex)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.08f)
                .background(background)
        ) {
            Row (Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){
                Spacer(modifier = Modifier.width(15.dp))
                Box(modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)){
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "", tint = Color.Black, modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            context.startActivity(Intent(context, ChatActivity::class.java))
                        })
                }
                Spacer(modifier = Modifier.width(5.dp))
                AsyncImage(model = dp, contentDescription = "",
                    Modifier
                        .size(45.dp)
                        .clip(CircleShape))
                Spacer(modifier = Modifier.width(15.dp))
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray))
        var padding=0.9f
        var lineCount by remember {
            mutableIntStateOf(1)
        }
        if (lineCount in 2..10){
            padding -= (lineCount * 0.015f)
        }
        if (showimageUpload){

            imagesendLoading()
        }
        else{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(padding)
                    .background(background)
            ) {

                if (res.item.isNotEmpty()) {

                    LazyColumn(state = lazyColumnListState) {

                        items(chatList) { chatItem ->
                            val currentDate = convertLongToDate(chatItem.item?.time!!)
                            if (currentDate != prevDate) {
                                Box (modifier = Modifier
                                    .fillMaxWidth()
                                    .height(25.dp), contentAlignment = Alignment.Center){

                                    Text(
                                        text = currentDate, color = Color.Black, fontSize = 10.sp)

                                    prevDate = currentDate
                                }
                            }

                            if (chatItem.item?.senderId == senderid) {
                                messageSender(itemState = chatItem)
                            } else {
                                MessageReceiver(itemState = chatItem, viewModel = viewModel, senderName = name)
                            }
                        }
                    }
                }

                if (res.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                if (res.error.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = res.error)
                    }
                }

            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(background),
            elevation = 50.dp
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color.LightGray)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(background)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 10.dp), contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(id = R.drawable.galleryicon),
                            contentDescription = "",
                            tint = main,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    launcher.launch("image/*")
                                })
                    }
                    Box(
                        modifier = Modifier
                            .background(background)
                            .fillMaxHeight(), contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = message.value,
                            onValueChange = { newText ->
                                message.value = newText
                                val maxLineLength = 25
                                lineCount = (newText.length + maxLineLength - 1) / maxLineLength
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 25.dp, start = 20.dp)
                                .fillMaxHeight(), cursorBrush = SolidColor(Color.Black)
                        ) {
                            if (message.value == "") {
                                Text(text = "write a message", color = Color.LightGray)
                            } else {
                                Text(text = message.value, color = Color.Black)

                            }
                        }
                    }
                    Box(Modifier.padding(end = 15.dp)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(main)
                                .clickable {

                                    scope.launch(Dispatchers.Main) {

                                        if (message.value.isNotEmpty()) {
                                            sendNotification(
                                                PushNotification(
                                                    NotificationData(
                                                        currentUserData[0].item?.name!!,
                                                        message.value
                                                    ), fcmtoken
                                                )
                                            )

                                            viewModel
                                                .insertMessage(
                                                    MessageResponse.MessageItems
                                                        (
                                                        senderId = senderid,
                                                        receiverId = id,
                                                        message = message.value,
                                                        category = "Exchange",
                                                        read = "false",
                                                        time = System.currentTimeMillis()
                                                    )
                                                )
                                                .collect {
                                                    when (it) {
                                                        is ResultState.Success -> {
                                                            context.showMsg(
                                                                msg = it.data
                                                            )
                                                            message.value = ""
                                                            lineCount = 1
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
                                        } else {
                                            context.showMsg("Message cannot be Empty")
                                        }

                                    }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "",
                                tint = betterWhite
                            )

                        }
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun messageSender(itemState: MessageResponse) {
    var lineCount by remember {
        mutableStateOf(1)
    }
    val context= LocalContext.current
    if (itemState.item?.message!="" && itemState.item?.images?.isEmpty() == true) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .padding(start = 60.dp, top = 2.dp, bottom = 2.dp, end = 10.dp)
                    .background(background)
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 15.dp)), contentAlignment = Alignment.TopEnd
            ) {
                if (itemState.item?.ad != null) {
                    Column(
                        modifier = Modifier
                            .background(msgSelf)
                            .padding(10.dp)
                            .clickable {
                                val intent = Intent(context, AdHostActivity::class.java)
                                intent.putExtra("clickedItem", itemState.item.ad)
                                context.startActivity(intent)
                            }, verticalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.fillMaxWidth(1f)) {
                            AsyncImage(
                                model = itemState.item.ad.item?.images?.get(0),
                                contentDescription = "",
                                modifier = Modifier.clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Text(text = itemState.item.ad.item?.title!!)
                            Text(text = itemState.item.ad.item.desc!!, maxLines = 2)
                            Text(text = itemState.item.message!!)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                            Row {
                                Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    painter = painterResource(R.drawable.tick),
                                    contentDescription ="tick",
                                    modifier =Modifier
                                        .size(15.dp),
                                    tint = if (itemState.item.read=="true") Color.Blue else Color.Black
                                )
                            }
                        }
                    }

                } else {
                    val maxLineLength = 25
                    var padding = 0.0f
                    lineCount =
                        (itemState.item?.message?.length!! + maxLineLength - 1) / maxLineLength
                    if (lineCount > 1) {
                        padding = 1f
                    }
                    Column(
                        modifier = Modifier
                            .background(msgSelf)
                            .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        if (lineCount > 1) {
                            Text(
                                text = itemState.item?.message!!,
                                modifier = Modifier.fillMaxWidth(padding)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                                Row {
                                    Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        painter = painterResource(R.drawable.tick),
                                        contentDescription ="tick",
                                        modifier =Modifier
                                            .size(15.dp),
                                        tint = if (itemState.item.read=="true") Color.Blue else Color.Black
                                    )
                                }
                            }
                        } else {
                            Text(text = itemState.item.message)
                            Box(modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                                Row {
                                    Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        painter = painterResource(R.drawable.tick),
                                        contentDescription ="tick",
                                        modifier =Modifier
                                            .size(15.dp),
                                        tint = if (itemState.item.read=="true") Color.Blue else Color.Black
                                    )
                                }
                            }
                        }

                    }
                }

            }
        }
    }else if (itemState.item?.images?.isNotEmpty()!!) {
        Box(
            modifier = Modifier
                .padding(start = 60.dp, top = 2.dp, bottom = 2.dp, end = 10.dp)
                .background(background)
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomStart = 15.dp)), contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(msgSelf)
                    .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (itemState.item.images.size>1) {
                    Box {
                        AsyncImage(
                            model = itemState.item.images[0],
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clickable {
                                    val intent = Intent(context, ImageHostActivity::class.java)
                                    intent.putStringArrayListExtra(
                                        "images",
                                        ArrayList(itemState.item.images)
                                    )
                                    intent.putExtra("sender", "You")
                                    intent.putExtra("time", itemState.item.time.toString())
                                    context.startActivity(intent)
                                }
                                .clip(RoundedCornerShape(15.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black
                                        )
                                    )
                                )

                        )
                        Box(contentAlignment = Alignment.BottomCenter,modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(top = 200.dp) ){
                            Text(text = "+${(itemState.item.images.size)-1} more Images",fontSize = 15.sp, color = betterWhite)
                        }
                    }
                }
                else{
                    AsyncImage(
                        model = itemState.item.images[0],
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable {
                                val intent = Intent(context, ImageHostActivity::class.java)
                                intent.putStringArrayListExtra(
                                    "images",
                                    ArrayList(itemState.item.images)
                                )
                                intent.putExtra("sender", "You")
                                intent.putExtra("time", itemState.item.time.toString())
                                context.startActivity(intent)
                            }
                            .clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp), contentAlignment = Alignment.BottomEnd){
                    Row {
                        Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            painter = painterResource(R.drawable.tick),
                            contentDescription ="tick",
                            modifier =Modifier
                                .size(15.dp),
                            tint = if (itemState.item.read=="true") Color.Blue else Color.Black
                        )
                    }
                }

            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageReceiver(itemState: MessageResponse,viewModel: ChatViewModel= hiltViewModel(),senderName:String) {
    var lineCount by remember {
        mutableStateOf(1)
    }
    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    LaunchedEffect(key1=true ){
        scope.launch(Dispatchers.Main) {
            viewModel.update(
                MessageResponse(item = MessageResponse.MessageItems(read = "true"),key = itemState.key)
            ).collect{
                when(it){
                    is ResultState.Success->{
                        context.showMsg(
                            msg=""
                        )
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
    if (itemState.item?.message!="" && itemState.item?.images?.isEmpty() == true) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 60.dp)
                    .background(background)
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp)), contentAlignment = Alignment.TopStart
            ) {
                if (itemState.item?.ad != null) {
                    Column(
                        modifier = Modifier
                            .background(msgreceive)
                            .padding(10.dp)
                            .clickable {
                                val intent = Intent(context, AdHostActivity::class.java)
                                intent.putExtra("clickedItem", itemState.item.ad)
                                context.startActivity(intent)
                            }, verticalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.fillMaxWidth(1f)) {
                            AsyncImage(
                                model = itemState.item.ad.item?.images?.get(0),
                                contentDescription = "",
                                modifier = Modifier.clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp, bottomStart = 15.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Text(text = itemState.item.ad.item?.title!!)
                            Text(text = itemState.item.ad.item.desc!!, maxLines = 2)
                            Text(text = itemState.item.message!!)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                            Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                        }
                    }

                } else {
                    val maxLineLength = 25
                    var padding = 0.0f
                    lineCount =
                        (itemState.item?.message?.length!! + maxLineLength - 1) / maxLineLength
                    if (lineCount > 1) {
                        padding = 1f
                    }
                    Column(
                        modifier = Modifier
                            .background(msgreceive)
                            .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        if (lineCount > 1) {
                            Text(
                                text = itemState.item?.message!!,
                                modifier = Modifier.fillMaxWidth(padding)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                                Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                            }
                        } else {
                            Text(text = itemState.item.message)
                            Box(modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .padding(end = 15.dp), contentAlignment = Alignment.CenterEnd){
                                Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                            }
                        }

                    }
                }

            }
        }
    }else if (itemState.item?.images?.isNotEmpty()!!) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 60.dp)
                .background(background)
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp, bottomEnd = 15.dp)), contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(msgreceive)
                    .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (itemState.item.images.size>1) {
                    Box {
                        AsyncImage(
                            model = itemState.item.images[0],
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .clickable {
                                    val intent = Intent(context, ImageHostActivity::class.java)
                                    intent.putStringArrayListExtra(
                                        "images",
                                        ArrayList(itemState.item.images)
                                    )
                                    intent.putExtra("sender", senderName)
                                    intent.putExtra("time", itemState.item.time.toString())
                                    context.startActivity(intent)
                                }
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black
                                        )
                                    )
                                )

                        )
                        Box(contentAlignment = Alignment.BottomCenter,modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(top = 200.dp) ){
                            Text(text = "+${(itemState.item.images.size)-1} more Images",fontSize = 15.sp, color = betterWhite)
                        }
                    }
                }
                else{
                    AsyncImage(
                        model = itemState.item.images[0],
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable {
                                val intent = Intent(context, ImageHostActivity::class.java)
                                intent.putStringArrayListExtra(
                                    "images",
                                    ArrayList(itemState.item.images)
                                )
                                intent.putExtra("sender", senderName)
                                intent.putExtra("time", itemState.item.time.toString())
                                context.startActivity(intent)
                            }
                            .clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp), contentAlignment = Alignment.BottomEnd){
                    Row {
                        Text(text = convertLongToTime(itemState.item?.time!!), fontSize = 9.sp)
                    }
                }

            }
        }
    }
}
private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
    try {
        val response = RetrofitInstance.api.postNotification(notification)
        if(response.isSuccessful) {
            Log.d(ContentValues.TAG, "Response")
        } else {
            Log.e(ContentValues.TAG, response.errorBody().toString())
        }
    } catch(e: Exception) {
        Log.e(ContentValues.TAG, e.toString())
    }
}
