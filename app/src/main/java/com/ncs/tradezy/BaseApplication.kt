package com.ncs.tradezy

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.google.firebase.messaging.BuildConfig
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(){
}