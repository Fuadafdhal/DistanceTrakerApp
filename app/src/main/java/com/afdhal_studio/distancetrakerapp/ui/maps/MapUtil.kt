package com.afdhal_studio.distancetrakerapp.ui.maps

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 *Created by Muh Fuad Afdhal on 19/05/2021
 */

object MapUtil {
    fun setCameraPosition(location: LatLng): CameraPosition {
        return CameraPosition.Builder()
            .target(location)
            .zoom(18f)
            .build()
    }
}