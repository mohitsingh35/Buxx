package com.ncs.tradezy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ncs.tradezy.ui.theme.background
import com.ncs.tradezy.ui.theme.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun loading(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}
@Composable
fun mainLoading() {
    val quotes = listOf("The more you share, the more you have", "In a world of 'use and toss,' be a 'reuse and sparkle' kind of person.", "Borrow, lend, repeat.", "Sell it : because your stuff wants to see the world, too", "Negotiations? What's the lowest you'll go?",
        "Selling items is like trying to find a date – it's all about the right profile picture",
        "Why buy when you can borrow",
        "Making life a little easier, one item at a time",
        )
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }
    var currentQuote by remember { mutableStateOf(quotes.random()) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentQuote = quotes.random()
        }
    }

    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = currentQuote,
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun internet() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.internet_error)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Sorry, we couldn't reach our servers", color = Color.LightGray, fontSize = 15.sp)
        Text(text = "Please check your Internet", color = Color.LightGray, fontSize = 15.sp)

    }
}
@Composable
fun emptyscreen() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(0.15f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.empty)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Text(text = "Oh no! Nothing here", color = Color.LightGray, fontSize = 15.sp)

    }
}

@Composable
fun maintenance() {
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.maintenance)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )


    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Sorry, we are undergoing maintenance", color = Color.LightGray, fontSize = 15.sp)
        Text(text = "We will be back soon", color = Color.LightGray, fontSize = 15.sp)
    }
}



@Composable
fun appUpdater(versionName: String) {
    var downloadStatus = remember { mutableStateOf(DownloadStatus.NotStarted) }

    fun downloadAPK(
        context: Context,
        url: String,
        onProgress: (Int) -> Unit
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val lengthOfFile = connection.contentLength
                val inputStream: InputStream = connection.inputStream

                val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
                val outputFile = File(externalDir, "Buxx_${versionName}.apk")

                val outputStream: OutputStream = outputFile.outputStream()

                val buffer = ByteArray(1024)
                var len1: Int
                var total = 0

                while (inputStream.read(buffer).also { len1 = it } > 0) {
                    total += len1
                    onProgress((total * 100 / lengthOfFile))
                    outputStream.write(buffer, 0, len1)
                }

                outputStream.close()
                inputStream.close()

                withContext(Dispatchers.Main) {
                    onProgress(100)
                    downloadStatus.value = DownloadStatus.Completed
                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    downloadStatus.value = DownloadStatus.Failed
                }
            }
        }
    }

    var appConfigList by remember { mutableStateOf(emptyList<AppConfigUpdater>()) }
    var progress by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var apkFile by remember { mutableStateOf<File?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (!context.packageManager.canRequestPackageInstalls()) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:${context.packageName}")),
            )
        }
    }
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }

    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    LaunchedEffect(true) {
        fetchDataFromFirebase { updatedList ->
            appConfigList = updatedList
        }
    }

    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(45.dp))
        Text(text = "Hooray A new version \nof the App is available", color = Color.DarkGray, fontSize = 18.sp)
        if (appConfigList.isEmpty()) {
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = "Looking for the latest version...", color = Color.LightGray, fontSize = 15.sp)
        } else {
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = "Version ${appConfigList[0].version}", color = Color.Gray, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "${appConfigList[0].logs}", color = Color.Gray, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(45.dp))
            var progress by remember { mutableStateOf(0) }
            var downloadJob: Job? by remember { mutableStateOf(null) }

            Button(
                onClick = {
                    downloadStatus.value = DownloadStatus.InProgress
                    downloadJob = downloadAPK(
                        context,
                        appConfigList[0].url!!,
                        onProgress = { progress = it },
                    )
                },
                enabled = downloadStatus.value == DownloadStatus.NotStarted
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Download APK")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (downloadStatus.value) {
                DownloadStatus.InProgress -> {
                    LinearProgressIndicator(progress = progress / 100f)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Downloading... $progress%")
                }
                DownloadStatus.Completed -> {
                    Text(text = "Download completed!")
                    Spacer(modifier = Modifier.height(16.dp))
                    val context = LocalContext.current
                    val uriHandler = LocalUriHandler.current
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Buxx_${versionName}.apk")
                    Button(
                        onClick = {
                            Log.d("file", file.absolutePath.toString())

                            initiateInstall(file, context)
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Install")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

enum class DownloadStatus {
    NotStarted,
    InProgress,
    Completed,
    Failed
}

private fun initiateInstall(file: File, context: Context) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val installIntent = Intent(Intent.ACTION_VIEW)
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive")
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(installIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun installApk(context: Context, apkFile: File) {
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        apkFile
    )

    val uriHandler = LocalUriHandler.current

    try {
        uriHandler.openUri(uri.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



private fun fetchDataFromFirebase(onDataFetched: (List<AppConfigUpdater>) -> Unit) {
    FirebaseDatabase.getInstance().reference.child("data").child("AppConfig")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedList = mutableListOf<AppConfigUpdater>()
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.getValue(AppConfigUpdater::class.java)
                    data?.let { updatedList.add(it) }
                }
                Log.d("updater", updatedList.toString())
                onDataFetched(updatedList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
}

@Composable
fun loadingdialog(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.smallloading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Hold On",)
                }
            }
        }
    })

}
@Composable
fun msgDialog(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.req)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    Text(text = "Hold On",)
                }
            }
        }
    })

}
@Composable
fun loadingdialog2(){
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.smallloading)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )
    AlertDialog(onDismissRequest = {}, confirmButton = { /*TODO*/ }, text = {
        Box(modifier = Modifier
            , contentAlignment = Alignment.Center){
            Column {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(250.dp)
                    )
                }
            }
        }
    })

}
@Composable
fun imagesendLoading() {
    val quotes = listOf("The more you share, the more you have", "In a world of 'use and toss,' be a 'reuse and sparkle' kind of person.", "Borrow, lend, repeat.", "Sell it : because your stuff wants to see the world, too", "Negotiations? What's the lowest you'll go?",
        "Selling items is like trying to find a date – it's all about the right profile picture",
        "Why buy when you can borrow",
        "Making life a little easier, one item at a time",
    )
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableStateOf(1f) }
    var currentQuote by remember { mutableStateOf(quotes.random()) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.imagesend)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = speed,
        restartOnPlay = false
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentQuote = quotes.random()
        }
    }

    Column(
        Modifier
            .background(background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(150.dp)
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = currentQuote,
                color = Color.LightGray,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}