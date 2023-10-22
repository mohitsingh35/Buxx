package com.ncs.tradezy

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.greenbg
import com.ncs.tradezy.ui.theme.main
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.TextStyle


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun addImages(navController: NavController,viewModel: AddScreenViewModel = hiltViewModel(),appContext:Context,viewModel2:BuyerLocationViewModel= hiltViewModel()){

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var desc by remember {  mutableStateOf("") }
    var price by remember {  mutableStateOf("") }
    var imageUris by remember { mutableStateOf(emptyList<Uri>()) }
    var isExchangeable by remember {  mutableStateOf(true) }
    var unpriced by remember {  mutableStateOf(false) }
    var whatsapp by remember {  mutableStateOf(false) }
    var buyerLocation by remember {  mutableStateOf("") }
    var buyerLocationDialog by remember {
        mutableStateOf(false)
    }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val maxImagesToSelect = 6
    var isLoading by remember {  mutableStateOf(false) }
    val scope= rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        uris?.let {
            imageUris = it.take(maxImagesToSelect)
        }
    }
    val focusRequester = remember { FocusRequester() }
    var userList=ArrayList<String>()
    var currentUserData=ArrayList<RealTimeUserResponse>()
    var showAdcontent by remember {
        mutableStateOf(false)
    }
    val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = appContext,
            oneTapClient = Identity.getSignInClient(appContext)
        )
    }
    val res=viewModel.res.value
    for (i in 0 until res.item.size){
        userList.add(res.item[i].item?.userId!!)
    }
    var showLoadingDialog by remember {
        mutableStateOf(false)
    }
    if (showLoadingDialog){
        loadingdialog()
    }
    val item= ArrayList<String>()
    val res2=viewModel2.res.value
    for (i in 0 until res2.item.size){
        item.add(res2.item[i].item?.list!!)
    }

    if (buyerLocationDialog) {
        if (item.isNotEmpty()) {
            Dialog(
                onDismissRequest = {
                    buyerLocationDialog = false
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White),
                ) {
                    Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(30.dp))
                        androidx.compose.material.Text(text = "Select the preferred buyer location ")
                        Spacer(modifier = Modifier.height(30.dp))
                        LazyColumn (modifier = Modifier.padding(start = 40.dp, end = 40.dp)){
                            items(1) {
                                for (i in 0 until item.size){
                                    eachRow(item =  item[i], onClick = {
                                        buyerLocation=item[i]
                                        buyerLocationDialog=false
                                    })
                                }

                            }
                        }
                    }
                }
            }
        }
        else{
            loadingdialog2()
        }


    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 30.dp, start = 20.dp, end = 20.dp)
            .background(background)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .padding(start = 10.dp)
                .clip(CircleShape)
                .clickable {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
            }
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)

            ) {
                Text(
                    text = "Sell",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        if (userList.contains(googleAuthUiClient.getSignedInUser()?.userID)) {
            if (isLoading) {
                mainLoading()
            } else {
                LazyColumn() {
                    item {
                        if (imageUris.isEmpty() && !showAdcontent) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Add Some photos of your product",
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "You can add upto 6 photos of your product.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Products with great images tends to receive the most views",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 100.dp), contentAlignment = Alignment.Center
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(painter = painterResource(id = R.drawable.addimages),
                                            contentDescription = "",
                                            Modifier
                                                .size(250.dp)
                                                .clickable {
                                                    launcher.launch("image/*")
                                                })
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "+",
                                            fontSize = 14.sp,
                                            color = main,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Add Photos",
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Light,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        else if (showAdcontent && imageUris.isNotEmpty()){
                            for (i in 0 until res.item.size){
                                if (res.item[i].item?.userId==googleAuthUiClient.getSignedInUser()?.userID){
                                    currentUserData.add(res.item[i])
                                }
                            }
                            var whatsappNumber by remember {  mutableStateOf(currentUserData[0].item?.phNumber) }
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            OutlinedTextField(
                                                value = title,
                                                onValueChange = {
                                                    if (it.length <= 50) {
                                                        title = it
                                                    }
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
                                                    Text(text = "Title")
                                                },
                                                shape = RoundedCornerShape(15.dp),
                                                maxLines = 2,
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Box(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(end = 20.dp), contentAlignment = Alignment.TopEnd){
                                                Text(
                                                    text = "${title.length}/50 ",
                                                    color = if (title.length > 50) Color.Red else Color.Gray,
                                                    modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Thin, fontSize = 16.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            OutlinedTextField(
                                                value = desc,
                                                onValueChange = {
                                                    if (it.length <= 300) {
                                                        desc = it
                                                    }
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
                                                    Text(text = "Description")
                                                },
                                                shape = RoundedCornerShape(15.dp),
                                                maxLines = 10,
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Box(modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(end = 20.dp), contentAlignment = Alignment.TopEnd){
                                                Text(
                                                    text = "${desc.length}/300 ",
                                                    color = if (desc.length > 300) Color.Red else Color.Gray,
                                                    modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Thin, fontSize = 16.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 5.dp)) {
                                                Row(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp), horizontalArrangement = Arrangement.Start) {
                                                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center){
                                                        Text(text = "keep this product unpriced?")
                                                    }
                                                    Checkbox(
                                                        checked = unpriced,
                                                        onCheckedChange = { unpriced = it }, colors = CheckboxDefaults.colors(checkedColor = main, checkmarkColor = betterWhite))
                                                }
                                                if (unpriced){
                                                    Text(text = "*keeping this checked would make your product available for free of cost", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Thin)
                                                    price=0.toString()
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            OutlinedTextField(
                                                value = price,
                                                onValueChange = {
                                                    price = it
                                                },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal).copy(
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        focusRequester.requestFocus()
                                                    }
                                                ),
                                                label = {
                                                    Text(text = "Price in ₹")
                                                },
                                                enabled = !unpriced,
                                                shape = RoundedCornerShape(15.dp),
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 5.dp)) {
                                                Row(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp), horizontalArrangement = Arrangement.Start) {
                                                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center){
                                                        Text(text = "is Exchangeable?")
                                                    }
                                                    Checkbox(
                                                        checked = isExchangeable,
                                                        onCheckedChange = { isExchangeable = it }, colors = CheckboxDefaults.colors(checkedColor = main, checkmarkColor = betterWhite))
                                                }
                                                if (isExchangeable){
                                                    Text(text = "*keeping this checked would make your ad exchange applicable", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Thin)

                                                }
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Box(modifier = Modifier
                                                .fillMaxWidth()){
                                                OutlinedTextField(
                                                    value = buyerLocation,
                                                    onValueChange = {
                                                        buyerLocation = it
                                                    },
                                                    readOnly = true,
                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                        imeAction = ImeAction.Next
                                                    ),
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            focusRequester.requestFocus()
                                                        }
                                                    ),
                                                    label = {
                                                        Text(text = "Buyer Location")
                                                    },
                                                    enabled = false,
                                                    shape = RoundedCornerShape(15.dp),
                                                    maxLines = 1,
                                                    modifier = Modifier
                                                        .fillMaxWidth().clickable {
                                                                                  buyerLocationDialog=true
                                                        },
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                                                    )
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 5.dp)) {
                                                Row(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(40.dp), horizontalArrangement = Arrangement.Start) {
                                                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center){
                                                        Text(text = "be contactable on Whatsapp?")
                                                    }
                                                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                                                        Image(
                                                            painter = painterResource(id = R.drawable.whatsapp),
                                                            contentDescription = "",
                                                            Modifier.size(25.dp)
                                                        )
                                                    }
                                                    Checkbox(
                                                        checked = whatsapp,
                                                        onCheckedChange = { whatsapp = it }, colors = CheckboxDefaults.colors(checkedColor = main, checkmarkColor = betterWhite))
                                                }
                                                if (whatsapp){
                                                    Text(text = "*by keeping this checked buyers would be able to contact you on Whatsapp", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Thin)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            if (whatsapp){
                                                Column {
                                                    OutlinedTextField(
                                                        value = whatsappNumber!!,
                                                        onValueChange = {
                                                            whatsappNumber = it
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
                                                            Text(text = "Whatsapp Number")
                                                        },
                                                        shape = RoundedCornerShape(15.dp),
                                                        maxLines = 1,
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            focusedLabelColor = Color.Black,
                                                            focusedLeadingIconColor = Color.Black,
                                                            focusedBorderColor = Color.Black,
                                                            focusedTextColor = Color.Black,
                                                            cursorColor = Color.Black,
                                                            unfocusedLabelColor = Color.Gray,
                                                            unfocusedBorderColor = Color.Gray,
                                                            unfocusedLeadingIconColor = Color.Gray
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = "*is your Whatsapp number correct?",
                                                        color = Color.Gray,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Thin
                                                    )
                                                    Spacer(modifier = Modifier.height(20.dp))
                                                }
                                            }
                                            var tags by remember { mutableStateOf(emptyList<String>()) }
                                            var text by remember { mutableStateOf(TextFieldValue()) }
                                            if (tags.isNotEmpty()){
                                                Column {
                                                    FlowRow(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(5.dp))
                                                            .background(background)
                                                            .padding(5.dp)
                                                    ) {
                                                        tags.forEachIndexed { index, tag ->

                                                            Box(modifier = Modifier
                                                                .fillMaxHeight()
                                                                .padding(end = 5.dp, bottom = 5.dp)
                                                                .clip(RoundedCornerShape(5.dp))
                                                                .clickable {
                                                                    tags = tags
                                                                        .toMutableList()
                                                                        .apply { removeAt(index) }
                                                                },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                eachtag(tag = tag)
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    Text(
                                                        text = "*click on individual tags to remove them",
                                                        color = Color.Gray,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Thin
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Column {
                                                OutlinedTextField(
                                                    value = text!!,
                                                    onValueChange = {
                                                        text = it
                                                        if (it.text.endsWith(" ")) {
                                                            val newTag = it.text.trim()
                                                            if (newTag.isNotEmpty()) {
                                                                tags = tags + newTag
                                                            }
                                                            text = TextFieldValue("")
                                                        }
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
                                                        Text(text = "Tags")
                                                    },
                                                    shape = RoundedCornerShape(15.dp),
                                                    maxLines = 1,
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedLabelColor = Color.Black,
                                                        focusedLeadingIconColor = Color.Black,
                                                        focusedBorderColor = Color.Black,
                                                        focusedTextColor = Color.Black,
                                                        cursorColor = Color.Black,
                                                        unfocusedLabelColor = Color.Gray,
                                                        unfocusedBorderColor = Color.Gray,
                                                        unfocusedLeadingIconColor = Color.Gray
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = "*tags are separated by space",
                                                    color = Color.Gray,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Thin
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = "*writing meaningful tags related to the product gets the maximum engagement",
                                                    color = Color.Gray,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Thin
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Box(Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .clickable {
                                                    if (title == "") {
                                                        context.showMsg("Title cannot be empty")
                                                    } else if (desc == "") {
                                                        context.showMsg("Description cannot be empty")
                                                    } else if (price == "") {
                                                        context.showMsg("Price cannot be empty")
                                                    } else if (buyerLocation == "") {
                                                        context.showMsg("Buyer Location cannot be empty")
                                                    } else if (tags.size < 3) {
                                                        context.showMsg("Atleast 3 tags are required")
                                                    } else if (whatsappNumber?.length != 10) {
                                                        context.showMsg("Error in Whatsapp Number")
                                                    } else if (title == "" || desc == "" || price == "" || buyerLocation == "" || tags.size < 3) {
                                                        context.showMsg("One or more fields are empty")
                                                    } else {
                                                        isLoading = true
                                                        scope.launch(Dispatchers.Main) {
                                                            viewModel
                                                                .insertAd(
                                                                    AdContent.AdContentItem(
                                                                        title = title,
                                                                        desc = desc,
                                                                        price = price.toInt(),
                                                                        time = System.currentTimeMillis(),
                                                                        isExchangeable = isExchangeable.toString(),
                                                                        buyerLocation = buyerLocation,
                                                                        sellerId = FirebaseAuth.getInstance().currentUser?.uid!!,
                                                                        viewCount = "0",
                                                                        trendingViewCount = "0",
                                                                        sold = "false",
                                                                        tags = tags,
                                                                        whatsapp = whatsapp.toString(),
                                                                        whatsappNum = whatsappNumber
                                                                    ),
                                                                    images = imageUris,
                                                                )
                                                                .collect {
                                                                    when (it) {
                                                                        is ResultState.Success -> {
                                                                            imageUris = emptyList()
                                                                            title = ""
                                                                            desc = ""
                                                                            price = ""
                                                                            buyerLocation = ""
                                                                            tags = emptyList()
                                                                            isLoading = false
                                                                            showAdcontent = false
                                                                            context.showMsg(
                                                                                msg = "Ad Posted Successfully"
                                                                            )
                                                                        }

                                                                        is ResultState.Failure -> {
                                                                            isLoading = false
                                                                            context.showMsg(
                                                                                msg = it.msg.toString()
                                                                            )
                                                                        }

                                                                        ResultState.Loading -> {
                                                                        }

                                                                        else -> {}
                                                                    }

                                                                }
                                                        }
                                                    }
                                                }
                                                .background(main), contentAlignment = Alignment.Center) {
                                                Row {
                                                    Text(
                                                        text = "Confirm",
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 20.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Image(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = "",
                                                        modifier = Modifier.size(25.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(40.dp))
                                        }
                                    }
                        }
                        else if (!showAdcontent && imageUris.isNotEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(
                                        text = "Awesome, the photos are here",
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "now just give some details of your product in the next step",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Thin, textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            val itemsPerRow = 2
                            val totalItems = imageUris.size
                            val gridHeight: Dp
                            if (totalItems == 1) {
                                gridHeight = with(LocalDensity.current) {
                                    val gridHeightDp = totalItems * 165.dp
                                    gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                                }
                            } else if (totalItems == 3) {
                                gridHeight = with(LocalDensity.current) {
                                    val gridHeightDp = 4 / itemsPerRow * 165.dp
                                    gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                                }
                            } else if (totalItems == 5) {
                                gridHeight = with(LocalDensity.current) {
                                    val gridHeightDp = 6 / itemsPerRow * 165.dp
                                    gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                                }
                            } else {
                                gridHeight = with(LocalDensity.current) {
                                    val gridHeightDp = totalItems / itemsPerRow * 165.dp
                                    gridHeightDp.toPx().coerceAtLeast(1f).toDp()
                                }
                            }
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(128.dp),
                                contentPadding = PaddingValues(
                                    start = 12.dp,
                                    top = 5.dp,
                                    end = 12.dp,
                                    bottom = 10.dp
                                ),
                                userScrollEnabled = false,
                                modifier = Modifier.height(gridHeight),
                                content = {
                                    items(imageUris) { uri ->
                                        val bitmap = loadImageBitmap(uri, context)
                                        bitmap?.let { btm ->
                                            Box(
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .aspectRatio(1f)
                                            ) {
                                                Image(
                                                    bitmap = btm.asImageBitmap(),
                                                    contentScale = ContentScale.Crop,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    showAdcontent = true
                                }
                                .background(main), contentAlignment = Alignment.Center) {
                                Row {
                                    Text(
                                        text = "Confirm",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Image(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "",
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable { launcher.launch("image/*") }
                                .background(Color.LightGray),
                                contentAlignment = Alignment.Center) {
                                Row {
                                    Text(
                                        text = "Retake",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Image(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "",
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
        else{
            Column(modifier = Modifier
                .background(background)
                .fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = "Please complete your profile to post an Ad", color = Color.Gray)
                }
            }
        }
    }
    }

fun loadImageBitmap(uri: Uri,context: Context): Bitmap? {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}
@Composable
fun eachRow(item:String,onClick:()->Unit){
    Column(
        Modifier
            .height(40.dp)
            .clickable { onClick() }
            .fillMaxWidth()) {
        Text(text = item, fontSize = 20.sp)
    }
}
//send sellerid
//enter token into database
//handle back click on mainactivity it goes to login screen
//handle back click on signout it goes again to mainactivity