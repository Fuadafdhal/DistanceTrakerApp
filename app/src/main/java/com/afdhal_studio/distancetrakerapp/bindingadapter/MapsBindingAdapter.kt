package com.afdhal_studio.distancetrakerapp.bindingadapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 *Created by Muh Fuad Afdhal on 19/05/2021
 */

class MapsBindingAdapter {
    companion object {
        @BindingAdapter("observeTracking")
        @JvmStatic
        fun observeTracking(view: View, started: Boolean) {
            if (started && view is Button) {
                view.visibility = View.VISIBLE
            } else if (started && view is TextView) {
                view.visibility = View.INVISIBLE
            }
        }
    }
}