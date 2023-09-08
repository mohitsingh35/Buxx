package com.ncs.tradezy

import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.greenbg
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(navController1: NavController,viewModel:HomeScreenViewModel= hiltViewModel()){

    val res=viewModel.res.value
    val newList=ArrayList<EachAdResponse>()
    for (i in 0 until res.item.size){
        if (res.item[i].item?.sold!="true"){
            newList.add(res.item[i])
        }
    }
    val filterbyTrending=newList.sortedByDescending { it.item?.trendingViewCount?.toInt() }
    val filterbyViews=newList.sortedByDescending { it.item?.viewCount?.toInt() }
    val navController= rememberNavController()

    Column(modifier = Modifier
        .background(background)
        .fillMaxSize()
        .padding(top = 30.dp, start = 20.dp)){
        NavigationSearchScreen(navController = navController, filterbyTrending = filterbyTrending, filterbyViews = filterbyViews )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun discoverarea(filterbyTrending: List<EachAdResponse>, filterbyViews:List<EachAdResponse>,navController: NavController){
    var text by remember {
        mutableStateOf("")
    }
    val context= LocalContext.current
    Column {
        Box(modifier = Modifier
            .padding(start = 10.dp)
            .clip(CircleShape)
            .clickable {
                context.startActivity(Intent(context,MainActivity::class.java))
            },
            contentAlignment = Alignment.Center){
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp)
            .clip(RoundedCornerShape(15.dp))){
            OutlinedTextField(value = text!!, onValueChange = { text = it }, label = {
                Text(
                    text = "Search"
                )
            }, enabled = false, leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
            },shape = RoundedCornerShape(15.dp), maxLines = 1, modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("search")
                }, colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
            ))
        }
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn(){
            items(1){
                Text(text = "Trending Now \uD83D\uDCC8 :", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                LazyRow(){
                    items(1){
                        val size=filterbyTrending.size
                        if (size>=3){
                            for (i in 0 until 3){
                                eachAd(item = filterbyTrending[i])
                            }
                        }
                        if (size<3){
                            for (i in 0 until size){
                                eachAd(item = filterbyTrending[i])
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = "Most Viewed \uD83D\uDC40 :", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                LazyRow(){
                    items(1){
                        val size=filterbyViews.size
                        if (size>=5){
                            for (i in 0 until 5){
                                eachAd(item = filterbyViews[i])
                            }
                        }
                        if (size<5){
                            for (i in 0 until size){
                                eachAd(item = filterbyViews[i])
                            }
                        }

                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun searcharea(navController: NavController) {
    var text by remember {
        mutableStateOf("")
    }
    val context= LocalContext.current.applicationContext
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }
    var recentSearches by remember {
        mutableStateOf(sharedPreferencesManager.getRecentSearches().toList())
    }

    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }
    BackHandler {
        navController.navigate("discover")
    }

    Column {
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .clip(CircleShape)
                .clickable {
                    navController.navigate("discover")
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp)
        ) {
            // Capture user input and update text
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    // You can update search suggestions based on the user's input here
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val search = text.trim()
                        if (search.isNotBlank()) {
                            sharedPreferencesManager.saveRecentSearch(search)
                            recentSearches = sharedPreferencesManager.getRecentSearches().toList()
                            text = ""
                        }
                    }
                ),

                label = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "")
                },
                shape = RoundedCornerShape(15.dp),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color.Black, focusedLeadingIconColor = Color.Black,focusedBorderColor = Color.Black, focusedTextColor = Color.Black, cursorColor = Color.Black, unfocusedLabelColor = Color.Gray, unfocusedBorderColor = Color.Gray, unfocusedLeadingIconColor = Color.Gray
                )
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Recent: ", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.padding(end = 20.dp)) {
                Text(
                    text = "Clear",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        recentSearches= emptyList()
                        sharedPreferencesManager.clearRecentSearches()
                    }
                )
            }
        }

        // Display recent searches in a LazyColumn
        LazyColumn {
            items(recentSearches) { search ->
                Text(
                    text = search,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            // Handle the selection of a recent search
                            text = search
                        }
                )
            }
        }

        // Display search suggestions based on user input
        // You can populate this LazyColumn with search suggestions based on the user's input
        LazyColumn {
            // Populate with search suggestions based on user input
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun eachAd(item:EachAdResponse) {
    val context= LocalContext.current
    var isClicked by remember {
        mutableStateOf(false)
    }
    if (isClicked){
        isClicked=false
        val intent = Intent(context, AdHostActivity::class.java)
        intent.putExtra("clickedItem", item)
        context.startActivity(intent)
    }
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .background(betterWhite)
            .clickable {
                isClicked = !isClicked
            }
            .fillMaxSize(),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(0.5.dp, Color.LightGray),
        elevation = 4.dp
    )  {
        Column {
            AsyncImage(model = item.item?.images?.get(0)!!, contentDescription = "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxHeight(0.6f))
            Column (Modifier.padding(10.dp)){
                Text(text = item.item.title!!, color = Color.Black, maxLines = 1)
                Text(text = item.item.desc!!,color= Color.Gray, fontSize = 12.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                    Box(modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            greenbg
                        ), contentAlignment = Alignment.Center){
                        Row (Modifier.padding(start = 5.dp, end = 5.dp)){
                            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "", tint = betterWhite, modifier = Modifier.size(10.dp))
                            Text(text = item.item.buyerLocation!!, color = betterWhite, fontSize = 8.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    if (item.item.exchangeable=="true"){
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .width(75.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                greenbg
                            ), contentAlignment = Alignment.Center){
                            Row {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "", tint = betterWhite, modifier = Modifier.size(10.dp))
                                Text(text = "Exchangeable", color = betterWhite, fontSize = 8.sp)
                            }
                        }
                    }
                    if (item.item.price==0){
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                greenbg
                            ), contentAlignment = Alignment.Center){
                            Row (Modifier.padding(start = 5.dp, end = 5.dp)){
                                Text(text = "₹ Free", color = betterWhite, fontSize = 8.sp)
                            }
                        }

                    }

                }
            }
        }
    }
    Spacer(modifier = Modifier.width(10.dp))
}
