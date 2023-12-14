package com.ncs.tradezy.googleAuth

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ncs.tradezy.MainActivity
import com.ncs.tradezy.R
import com.ncs.tradezy.TextAnimation
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.main

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun googleScreen(
    state: GoogleSignInState,
    navController: NavController,
    onSignInClick:()->Unit
){

    BackHandler() {

    }

    val context= LocalContext.current
    LaunchedEffect(key1 = state.signInError){
        state.signInError?.let { error ->
            Toast.makeText(context,error,Toast.LENGTH_LONG).show()
        }
    }
    Column( modifier = Modifier
        .fillMaxSize()
        .background(background)
        .padding(16.dp), verticalArrangement = Arrangement.Center) {
        Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            TextAnimation("Lets Trade \uD83E\uDD11 ","Lets Sell \uD83D\uDCB0 ", counter = 1)
            Spacer(modifier = Modifier.height(100.dp))
            Image(painter = painterResource(id = R.drawable.pana), contentDescription = "" ,Modifier.height(345.dp))
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Got something to sell? \n Connect with potential buyers around you", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(40.dp))
            Box(Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(5.dp))
                .clickable { navController.navigate("about3") }
                .background(main), contentAlignment = Alignment.Center) {
                Row {
                    Text(text = "Next", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(25.dp) )
                }
            }
        }
    }

}

@Composable
fun about3(
    navController: NavController,

){

    val context= LocalContext.current

    Column( modifier = Modifier
        .fillMaxSize()
        .background(background)
        .padding(16.dp), verticalArrangement = Arrangement.Center){
        Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Lets Buy \uD83D\uDED2", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Spacer(modifier = Modifier.height(100.dp))
            Image(painter = painterResource(id = R.drawable.buy), contentDescription = "" )
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Don't want to spend more? \n Explore a wide range from sellers around you", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(40.dp))
            Box(Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(5.dp))
                .clickable { navController.navigate("about2") }
                .background(main), contentAlignment = Alignment.Center) {
                Row {
                    Text(text = "Next", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(25.dp) )
                }
            }
        }
    }

}
@Composable
fun splash(navController: NavController,isSigned:Boolean){
    var visibility by remember { mutableStateOf(false) }
    if (!isSigned){
        LaunchedEffect(key1 = true){
            delay(3000L)
            navController.navigate("sign_in")
        }

    }
    else{
        val context= LocalContext.current
        LaunchedEffect(key1 = true){
            delay(2000L)
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.Center){
        Image(painter = painterResource(id = R.drawable.applogo), contentDescription = "", modifier = Modifier.size(200.dp))

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun about2(
    navController: NavController,
    onSignInClick:()->Unit
){
    val context= LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(background)

    ) { contentPadding ->
        Column( modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(16.dp), verticalArrangement = Arrangement.Center) {
            Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier
                    .padding(contentPadding))
                Text(text = "Lets Exchange \uD83D\uDD01", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(100.dp))
                Image(painter = painterResource(id = R.drawable.exchange), contentDescription = "",Modifier.height(345.dp) )
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = "Don't want to sell? \n Get the best value around you", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(40.dp))
                Box(Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { showBottomSheet = true }
                    .background(main), contentAlignment = Alignment.Center) {
                    Row {
                        Text(text = "Get Started", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(25.dp) )
                    }
                }
            }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            Text(text = "Begin your journey \uD83C\uDFA2", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 25.sp)
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(text = "Give a right place to your things \n or explore a wide range of products!", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 15.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(30.dp))
                            Box(Modifier
                                .fillMaxWidth(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    onSignInClick()
                                    scope
                                        .launch { sheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                }
                                .background(main), contentAlignment = Alignment.Center) {
                                Row {
                                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center){
                                        Image(painter = painterResource(id = R.drawable.googleicon), contentDescription = "" ,Modifier.size(35.dp))
                                    }
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Image(painter = painterResource(id = R.drawable.google), contentDescription = "", Modifier.size(55.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(text = "*by continuing you Accept our Terms and Privacy Policies", color = Color.LightGray, fontWeight = FontWeight.Medium, fontSize = 12.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(text = "With ❤️ by Buxx", color = Color.LightGray, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, textAlign = TextAlign.Center)

                        }
                    }
            }
        }
    }

    }

}