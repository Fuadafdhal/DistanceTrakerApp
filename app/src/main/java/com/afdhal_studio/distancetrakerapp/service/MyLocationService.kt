package com.afdhal_studio.distancetrakerapp.service

import android.content.Intent
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.afdhal_studio.distancetrakerapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

/**
 *Created by Muh Fuad Afdhal on 01/06/2021
 */

class MyLocationService : LifecycleService() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object {
        val mLocation = MutableLiveData<LatLng>()
        val mAddress = MutableLiveData<String>()
    }


    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_SERVICE_START -> {
                    val text = it.getStringExtra(Intent.EXTRA_TEXT)
                    Log.d("MyLocationService", "ACTION_SERVICE_START")
                    Log.d("MyLocationService", "text")
                    val latitude = intent.getDoubleExtra(Constants.LOCATION_LAT_EXTRA, -1.0)
                    val longitude = intent.getDoubleExtra(Constants.LOCATION_LNG_EXTRA, -1.0)


                    val locale = Locale("id", "ID")
                    val geocoder = Geocoder(this, locale)


                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                        if (addresses == null || addresses.size == 0) {
                            Log.d("MyLocationService", "No Location")

                        } else {
                            mLocation.postValue(LatLng(latitude, longitude))
                            val resultAddress = StringBuilder()
                            val address = addresses[0]

                            for (i in 0..address.maxAddressLineIndex) {
                                if (i == address.maxAddressLineIndex) {
                                    resultAddress.append(address.getAddressLine(i))
                                } else {
                                    resultAddress.append(address.getAddressLine(i))
                                }
                            }
                            mAddress.postValue(resultAddress.toString())
                            Log.d("MyLocationService", resultAddress.toString())
                        }


                    } catch (ioException: IOException) {
                        // Catch network or other I/O problems.
//                        errorMessage1 = getString(R.string.no_network_connection)
//                        errorMessage2 = getString(R.string.service_not_available)

                        Log.e("MyLocationService", ioException.message.toString())
                    } catch (e: Exception) {
                        Log.e("MyLocationService", e.message.toString())
                    }
                }
                Constants.ACTION_SERVICE_STOP -> {

                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}