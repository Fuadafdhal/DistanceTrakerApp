package com.afdhal_studio.distancetrakerapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.afdhal_studio.distancetrakerapp.utils.Utils.ACTION_SERVICE_START
import com.afdhal_studio.distancetrakerapp.utils.Utils.ACTION_SERVICE_STOP
import com.afdhal_studio.distancetrakerapp.utils.Utils.NOTIFICATION_CHANNEL_ID
import com.afdhal_studio.distancetrakerapp.utils.Utils.NOTIFICATION_CHANNEL_NAME
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 *Created by Muh Fuad Afdhal on 19/05/2021
 */

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    companion object {
        val started = MutableLiveData<Boolean>()
        val startTime = MutableLiveData<Long>()
        val stopTime = MutableLiveData<Long>()

        val locationList = MutableLiveData<MutableList<LatLng>>()
    }

    private fun setInitialValues() {
        started.postValue(false)
    }


    override fun onCreate() {
        setInitialValues()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    started.postValue(true)
                }
                ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                }
                else -> {
                }
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}