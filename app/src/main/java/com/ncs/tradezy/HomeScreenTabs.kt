package com.ncs.tradezy


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState,navController: NavController) {

    val list = listOf(
        "All Items","Buy Only","Exchange")
    val scope = rememberCoroutineScope()
    Column {


        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = primary,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color = Color.White
                )
            }
        ) {
            list.forEachIndexed { index, _ ->
                Tab(
                    text = {
                        Text(
                            list[index],
                            color = if (pagerState.currentPage == index) Color.White else Color.LightGray
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        Box(modifier = Modifier.padding(start = 10.dp, end = 10.dp,top=10.dp)){
            searchbar(navController = navController)
        }
    }
}
@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState,token: String, filterList: ArrayList<RealTimeUserResponse> = ArrayList(),navController: NavController) {

    HorizontalPager(state = pagerState) {
            page ->
        when (page) {
            0 -> AllItems(token = token , filteredList = filterList, navController = navController)
            1 -> BuyOnly()
            2 -> Exchange()
        }
    }
}
