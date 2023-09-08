package com.ncs.tradezy

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primaryTheme

class ImageHostActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            primaryTheme {
                val images = intent.getStringArrayListExtra("images")
                val name = intent.getStringExtra("sender")
                val time = intent.getStringExtra("time")


                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(betterWhite), contentAlignment = Alignment.Center){
                    Column (Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.1f)
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
                                            this@ImageHostActivity.startActivity(
                                                Intent(
                                                    this@ImageHostActivity,
                                                    ChatActivity::class.java
                                                )
                                            )
                                        })
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                Column {
                                    Text(text = name!!, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                    Row {
                                        Text(text = convertLongToDate(time!!.toLong()), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(text = convertLongToTime(time!!.toLong()), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)

                                    }

                                }
                            }
                        }
                        imageHost(images = images!!)

                    }
                }
            }
        }
    }
}

@Composable
fun imageHost(images:List<String>){
    LazyRow(modifier = Modifier.fillMaxWidth()){
        items(1){
            for (i in 0 until images.size){
                AsyncImage(model = images[i], contentDescription = "", modifier = Modifier.padding(5.dp).fillMaxSize(), contentScale = ContentScale.Fit)
            }
        }
    }
}

