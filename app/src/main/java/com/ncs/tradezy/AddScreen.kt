package com.ncs.tradezy

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.ncs.marketplace.googleAuth.GoogleAuthUIClient
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(viewModel: AddScreenViewModel = hiltViewModel(), appContext: Context,navController: NavController){
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf(emptyList<Uri>()) }
    var title by remember { mutableStateOf("") }
    var desc by remember {  mutableStateOf("") }
    var price by remember {  mutableStateOf("") }
    var isExchangeable by remember {  mutableStateOf(true) }
    var isLoading by remember {  mutableStateOf(false) }
    var buyerLocation by remember {  mutableStateOf("") }
    val scope= rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var userList=ArrayList<String>()
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
    if (userList.contains(googleAuthUiClient.getSignedInUser()?.userID)){
        Column(modifier = Modifier.background(primary)) {
            setActionBar(screenName = "Post an Ad", R.drawable.ic_launcher_foreground,navController)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    items(1) {
                        Column {
                            val launcher =
                                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
                                    uris?.let {
                                        imageUris = it
                                    }
                                }

                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.Gray)
                                .clickable {
                                    launcher.launch("image/*")
                                }) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (imageUris.isEmpty()) {
                                        Image(
                                            painter = painterResource(id = R.drawable.gallery),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                        )
                                    }
                                    LazyRow(
                                        Modifier
                                            .fillMaxSize()
                                    ) {
                                        items(imageUris) { uri ->
                                            bitmap = loadImageBitmap(uri, context)
                                            bitmap?.let { btm ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(2.dp)
                                                )
                                                Image(
                                                    bitmap = btm.asImageBitmap(),
                                                    contentScale = ContentScale.Crop,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(300.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(50.dp))
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text(text = "Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                OutlinedTextField(
                                    value = desc,
                                    onValueChange = { desc = it },
                                    label = { Text(text = "Description") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    label = { Text(text = "Price") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "is Exchangeable")
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Checkbox(
                                        checked = isExchangeable,
                                        onCheckedChange = { isExchangeable = it })
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                OutlinedTextField(
                                    value = buyerLocation,
                                    onValueChange = { buyerLocation = it },
                                    label = { Text(text = "Buyer Location") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(onClick = {
                                    isLoading = true
                                    scope.launch(Dispatchers.Main) {
                                        viewModel.insertAd(
                                            AdContent.AdContentItem(
                                                title = title,
                                                desc = desc,
                                                price = price.toInt(),
                                                time = System.currentTimeMillis(),
                                                isExchangeable = isExchangeable.toString(),
                                                buyerLocation = buyerLocation,
                                                sellerId = FirebaseAuth.getInstance().currentUser?.uid!!,
                                                viewCount = "0",
                                                trendingViewCount = "0"
                                            ),
                                            images = imageUris,
                                        ).collect {
                                            when (it) {
                                                is ResultState.Success -> {
                                                    isLoading = false
                                                    context.showMsg(
                                                        msg = it.data
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
                                            }

                                        }
                                    }

                                }) {
                                    Text(text = "Submit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    else{
        Column(modifier = Modifier
            .background(primary)
            .fillMaxSize()) {
            setActionBar(screenName = "Post an Ad", R.drawable.ic_launcher_foreground,navController)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(text = "Please complete your profile to post an Ad", color = accent)
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

//send sellerid
//enter token into database
//handle back click on mainactivity it goes to login screen
//handle back click on signout it goes again to mainactivity