package com.afdhal_studio.distancetrakerapp.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.afdhal_studio.distancetrakerapp.R
import com.afdhal_studio.distancetrakerapp.databinding.FragmentMapsBinding
import com.afdhal_studio.distancetrakerapp.service.TrackerService
import com.afdhal_studio.distancetrakerapp.utils.ExtensionFunctions.disable
import com.afdhal_studio.distancetrakerapp.utils.ExtensionFunctions.hide
import com.afdhal_studio.distancetrakerapp.utils.ExtensionFunctions.show
import com.afdhal_studio.distancetrakerapp.utils.Permissions.hasBackgroundLocationPermission
import com.afdhal_studio.distancetrakerapp.utils.Permissions.requestBackgroundLocationPermission
import com.afdhal_studio.distancetrakerapp.utils.Constants.ACTION_SERVICE_START

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, EasyPermissions.PermissionCallbacks {
    companion object {
        const val TAG = "MapsFragment"
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

//    val started = MutableLiveData(false)

//    private var startTime = 0L
//    private var stopTime = 0L

    private var locationList = mutableListOf<LatLng>()
//    private var polylineList = mutableListOf<Polyline>()
//    private var markerList = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.startButton.setOnClickListener {
            onStartButtonClicked()
        }
        binding.stopButton.setOnClickListener {

        }
        binding.resetButton.setOnClickListener {

        }
    }

    private fun onStartButtonClicked() {
        if (hasBackgroundLocationPermission(requireContext())) {
            startCountDown()
            binding.startButton.disable()
            binding.startButton.hide()
            binding.stopButton.show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    private fun startCountDown() {
        binding.timerTextView.show()
        binding.stopButton.disable()
        val timer: CountDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val currentSecond = millisUntilFinished / 1000
                if (currentSecond.toString() == "0") {
                    binding.timerTextView.text = getString(R.string.go)
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                } else {
                    binding.timerTextView.text = currentSecond.toString()
                    binding.timerTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            }

            override fun onFinish() {
                sendActionCommandToService(ACTION_SERVICE_START)
                binding.timerTextView.hide()
            }
        }
        timer.start()
    }

    private fun sendActionCommandToService(action: String) {
        Intent(requireContext(), TrackerService::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.uiSettings.apply {
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isCompassEnabled = false
            isScrollGesturesEnabled = false
        }
        observeTrackerService()
    }


    private fun observeTrackerService() {
        TrackerService.locationList.observe(viewLifecycleOwner) {
            if (it != null) {
                locationList = it
                Log.d("MapsFragment", locationList.toString())
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms[0])) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        onStartButtonClicked()
    }

    override fun onMyLocationButtonClick(): Boolean {
        binding.hintTextView.animate().alpha(0f).duration = 1500
        lifecycleScope.launch {
            delay(2500)
            binding.hintTextView.hide()
            binding.startButton.show()
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}