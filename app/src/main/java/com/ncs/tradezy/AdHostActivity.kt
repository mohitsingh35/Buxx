package com.ncs.tradezy

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.ncs.tradezy.ui.theme.adhostTheme
import com.ncs.tradezy.ui.theme.primaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdHostActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            adhostTheme {
                val clickedItem = intent.getSerializableExtra("clickedItem") as? EachAdResponse
                adHost(item1 = clickedItem!!){
                    val mob: String = clickedItem.item!!.whatsappNum!!
                    val mobile=mob.toLong()
                    val mobileNumber="+91$mobile".toLong()
                    val message: String = it
//                    val installed = appInstalledOrNot("com.whatsapp")
//                    if (installed) {
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.data =
//                            Uri.parse("http://api.whatsapp.com/send?phone=+91$mobileNumber&text=$message")
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(
//                            this,
//                            "Whats app not installed on your device",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
                    try {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, message)
                            putExtra("jid", "$mobileNumber@s.whatsapp.net")
                            type = "text/plain"
                            setPackage("com.whatsapp")
                        }
                        startActivity(sendIntent)
                    }catch (e: Exception){
                        e.printStackTrace()
                        val appPackageName = "com.whatsapp"
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                        } catch (e :android.content.ActivityNotFoundException) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                        }
                    }
                }
            }
        }
    }
    private fun appInstalledOrNot(url: String): Boolean {
        val packageManager: PackageManager = packageManager
        val app_installed: Boolean = try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }
}

