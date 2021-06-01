package com.afdhal_studio.distancetrakerapp.ui.mylocation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afdhal_studio.distancetrakerapp.R
import com.afdhal_studio.distancetrakerapp.databinding.FragmentMyLocationBinding
import com.afdhal_studio.distancetrakerapp.service.MyLocationService
import com.afdhal_studio.distancetrakerapp.service.TrackerService
import com.afdhal_studio.distancetrakerapp.utils.Constants
import com.afdhal_studio.distancetrakerapp.utils.Constants.ACTION_SERVICE_START
import com.afdhal_studio.distancetrakerapp.utils.Permissions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class MyLocationFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentMyLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    private lateinit var addressOutput: String
    private lateinit var subAdminLocationOutput: String
    private var addressResultCode: Int? = 0
    private var isSupportedArea: Boolean? = false
    private val mSupportedArea = arrayOf<String>()


    private var myDeviceLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.buttonMyLocation.setOnClickListener {
            getDeviceLocation()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.mMap = googleMap

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            getDeviceLocation()
        }
        mMap.setOnCameraIdleListener {
            sendActionCommandToService(ACTION_SERVICE_START)
        }
        observeTrackerService()
    }

    private fun sendActionCommandToService(action: String) {
        val currentMarkerPosition = mMap.cameraPosition.target

        Intent(requireContext(), MyLocationService::class.java).apply {
            this.action = action
            putExtra(Constants.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude)
            putExtra(Constants.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude)
            requireContext().startService(this)
        }
    }


    private fun observeTrackerService() {
        MyLocationService.mLocation.observe(viewLifecycleOwner){
            binding.textLatLang.text = "${it.latitude},${it.longitude}"
        }

        MyLocationService.mAddress.observe(viewLifecycleOwner){
            binding.textAddress.text = it
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms[0])) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Permissions.requestBackgroundLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onMyLocationButtonClick(): Boolean {
        return true
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            val location = mFusedLocationProviderClient.lastLocation
            location.addOnSuccessListener {
                if (it != null) {
                    val deviceLatLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deviceLatLng, 18f))
                } else {
                    val locationRequest = LocationRequest.create()
                    locationRequest.interval = 1000
                    locationRequest.fastestInterval = 5000
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            val deviceLatLng = LatLng(
                                locationResult.lastLocation.latitude,
                                locationResult.lastLocation.longitude
                            )
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deviceLatLng, 18f))
                            mFusedLocationProviderClient.removeLocationUpdates(
                                locationCallback
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MyLocationFragment", "getDeviceLocation: : " + e.message)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendActionCommandToService(Constants.ACTION_SERVICE_STOP)
    }
}