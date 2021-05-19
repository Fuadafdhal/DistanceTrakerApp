package com.afdhal_studio.distancetrakerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *Created by Muh Fuad Afdhal on 20/05/2021
 */

@Parcelize
data class Result(
    var distance: String,
    var time: String
): Parcelable