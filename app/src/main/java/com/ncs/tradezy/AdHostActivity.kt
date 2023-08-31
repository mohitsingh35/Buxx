package com.ncs.tradezy

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ncs.tradezy.ui.theme.HostelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdHostActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HostelTheme {
                val clickedItem = intent.getSerializableExtra("clickedItem") as? EachAdResponse
                adHost(item = clickedItem!!)

            }
        }
    }
}

