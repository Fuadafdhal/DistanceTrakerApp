package com.afdhal_studio.distancetrakerapp.utils

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.afdhal_studio.distancetrakerapp.utils.Utils.PERMISSION_LOCATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

/**
 *Created by Muh Fuad Afdhal on 18/05/2021
 */

object Permissions {
    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    fun requestLocationPermission(fragment: Fragment){
        EasyPermissions.requestPermissions(
            fragment,
            "This application cannot work without Location Permission",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


}