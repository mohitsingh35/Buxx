package com.ncs.tradezy

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.secondary


//23-08-2023 8:00 P.M.

@Composable
fun setActionBar(screenName:String, image: Int){
    val context= LocalContext.current
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(primary)

        ) {

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(Modifier.padding(15.dp)){
                    Text(
                        text = screenName,
                        color = accent,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )

                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                ) {
                    Image(painterResource(id = image), contentDescription = "",Modifier.clickable {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    })
                }
            }

        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(secondary)) {
        }
    }
}