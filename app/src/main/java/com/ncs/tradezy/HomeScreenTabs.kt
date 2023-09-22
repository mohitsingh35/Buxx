package com.ncs.tradezy


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.primary
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState,navController: NavController) {

    val list = listOf(
        "All Items","Buy Only","Exchange")
    val ic_list = listOf(
        R.drawable.all_ic,R.drawable.buy_ic,R.drawable.exchange_ic)
    val scope = rememberCoroutineScope()
    Column {


        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = background,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 2.dp,
                    color = Color.Black,
                )
            }
        ) {
            list.forEachIndexed { index, _ ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.height(50.dp)
                    ) {
                        if (pagerState.currentPage == index) {
                            Icon(
                                painter = painterResource(id = ic_list[index]),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = list[index],
                            color = if (pagerState.currentPage == index) Color.Black else Color.Gray
                        )
                    }
                }

            }
        }
        Box(modifier = Modifier.padding(start = 10.dp, end = 10.dp)){
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
