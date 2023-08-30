package com.ncs.tradezy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ncs.tradezy.ui.theme.accent
import com.ncs.tradezy.ui.theme.betterWhite
import com.ncs.tradezy.ui.theme.primary
import com.ncs.tradezy.ui.theme.secondary
@Composable
fun HomeScreen(){
    Column(modifier = Modifier.background(primary).fillMaxSize()){

        val context= LocalContext.current
        val items= listOf(
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,""),
            ItemContent(listOf( R.drawable.amg),"Lamborghini","Lamborghini",9000,1692814788107,true,""),
            ItemContent(listOf( R.drawable.amg),"Buggati","Lamborghini",9000,1692814788110,true,""),
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,""),
            ItemContent(listOf( R.drawable.amg),"Mercedes AMG","Mercedes",9000,1692814763079,true,"")
        )
            val newList = items.sortedByDescending { it.time  }
            setActionBar(screenName = "Home", image = R.drawable.ic_launcher_foreground)
            Box (Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp)) {
                LazyColumn {
                    items(1){
                        itemHolder(items = newList )
                    }
                }
            }

    }
}

@Composable
fun SearchScreen(){
    Column(modifier = Modifier.background(primary)){
        setActionBar(screenName = "Search", image = R.drawable.ic_launcher_foreground)
    }
}

