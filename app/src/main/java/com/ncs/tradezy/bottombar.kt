package com.ncs.tradezy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.secondary


@Composable
fun bottomBar(
    items:List<BottomBarContent>,
    navController: NavController,
    onItemClick: (BottomBarContent) -> Unit
){
    val backStackEntry=navController.currentBackStackEntryAsState()
    Column (Modifier.fillMaxWidth()){
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(secondary)) {
        }
        Row (
            modifier = Modifier
                .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            items.forEach{item ->
                val state=item.route==backStackEntry.value?.destination?.route
                BottomBarItem(item = item, state = state, onItemClick = {onItemClick(item)})
            }

        }
    }
}
@Composable
fun BottomBarItem(
    item: BottomBarContent,
    state:Boolean=false,
    selectedColor: Color = accent,
    onItemClick:()-> Unit
){
    Row() {
        Box(modifier = Modifier.size(33.dp).clip(RoundedCornerShape(33.dp)).clickable { onItemClick() }){
            Icon(
                painter = painterResource(item.iconId),
                contentDescription ="item",
                modifier = Modifier
                    .size(33.dp)
                    ,
                tint = if (state) selectedColor else Color.Gray,
            )
        }


    }

}