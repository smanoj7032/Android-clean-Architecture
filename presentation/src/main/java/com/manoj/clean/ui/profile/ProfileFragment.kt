package com.manoj.clean.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentProfileBinding
import com.manoj.clean.ui.common.base.BaseFragment
import com.manoj.clean.ui.common.base.common.permissionutils.runWithPermissions
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.geolocator.LocationTrackerWorker
import com.manoj.clean.util.geolocator.NotificationWorker
import com.manoj.clean.util.geolocator.geofencer.Geofencer
import com.manoj.clean.util.geolocator.geofencer.models.Geofence
import com.manoj.clean.util.geolocator.misc.hideKeyboard
import com.manoj.clean.util.geolocator.misc.requestFocusWithKeyboard
import com.manoj.clean.util.geolocator.misc.showGeofenceInMap
import com.manoj.clean.util.geolocator.sharedPreferences
import com.manoj.clean.util.geolocator.tracking.LocationTracker
import com.manoj.clean.util.getBackgroundLocationPermission
import com.manoj.clean.util.getLocationPermissions
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), GoogleMap.OnMarkerClickListener,OnMapReadyCallback {
    private val viewModel: ProfileViewModel by viewModels()
    private var geofence = Geofence()
    private var map: GoogleMap? = null
    private val preferenceChangedListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            Log.v("", "OnSharedPreferenceChange key=$key")

            // key has been updated
            if (key == LocationTrackerWorker.USER_LOCATION_KEY) {

                // retrieve location from preferences
                val locationResult = sharedPreferences.getString(key, null)
                Log.v("", "OnSharedPreferenceChange 1 $locationResult")
            }
        }

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    override fun inflateViewBinding(inflater: LayoutInflater): FragmentProfileBinding =
        FragmentProfileBinding.inflate(inflater)

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() =runWithPermissions(*getLocationPermissions()){
        binding.setup()
        sharedPreferences?.registerOnSharedPreferenceChangeListener(preferenceChangedListener)
    }
    private fun checkNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           runWithPermissions(Manifest.permission.POST_NOTIFICATIONS){
               permission()
           }
        } else {
           permission()
        }
    }
    private fun permission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runWithPermissions(*getBackgroundLocationPermission()){
                setLocation()
            }
        }else{
          setLocation()
        }
    }
    private fun setLocation(){
        LocationTracker.removeLocationUpdates(requireContext())
        LocationTracker.requestLocationUpdates(requireContext(), LocationTrackerWorker::class.java)
        val location = getLastKnownLocation()
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
        showGeofences()
    }

    @SuppressLint("MissingPermission")
    private fun FragmentProfileBinding.setup() {
        fabCurrentLocation.setOnClickListener {
            val location = getLastKnownLocation()
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

        fabNewReminder.setOnClickListener {
            showConfigureLocationStep()
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        mapFragment.getMapAsync(this@ProfileFragment)
    }



    @SuppressLint("MissingPermission")
    private fun addGeofence(geofence: Geofence) {
        Geofencer(requireContext())
            .addGeofenceWorker(geofence, NotificationWorker::class.java) {
                binding.container2.isGone = true
                showGeofences()
            }


    }

    private fun showGeofenceUpdate() {
        map?.clear()
        showGeofenceInMap(requireContext(), map!!, geofence)
    }

    private fun showGeofences() {
        map?.run {
            clear()
            for (geofence in Geofencer(requireContext()).getAll()) {
                showGeofenceInMap(requireContext(), this, geofence)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val locationManager = requireContext().getSystemService<LocationManager>() ?: return null
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        return bestLocation
    }


    private fun FragmentProfileBinding.showGeofenceRemoveAlert(geofence: Geofence) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.run {
            setMessage(getString(R.string.reminder_removal_alert))
            setButton(
                AlertDialog.BUTTON_POSITIVE,
                getString(R.string.reminder_removal_alert_positive)
            ) { dialog, _ ->
                removeGeofence(geofence)
                dialog.dismiss()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getString(R.string.reminder_removal_alert_negative)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun FragmentProfileBinding.removeGeofence(geofence: Geofence) {
        Geofencer(requireContext()).removeGeofence(geofence.id) {
            showGeofences()
            Snackbar.make(
                main,
                R.string.reminder_removed_success, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun FragmentProfileBinding.showConfigureLocationStep() {
        container2.isVisible = true
        marker.isVisible = true
        instructionTitle.isVisible = true
        instructionSubtitle.isVisible = true
        radiusBar.isGone = true
        radiusDescription.isGone = true
        message.isGone = true
        instructionTitle.text = getString(R.string.instruction_where_description)
        next.setOnClickListener {
            geofence.latitude = map?.cameraPosition?.target?.latitude ?: 0.0
            geofence.longitude = map?.cameraPosition?.target?.longitude ?: 0.0
            showConfigureRadiusStep()
        }
        showGeofenceUpdate()
    }

    private fun FragmentProfileBinding.showConfigureRadiusStep() {
        marker.isGone = true
        instructionTitle.isVisible = true
        instructionSubtitle.isGone = true
        radiusBar.isVisible = true
        radiusDescription.isVisible = true
        message.isGone = true
        instructionTitle.text = getString(R.string.instruction_radius_description)
        next.setOnClickListener {
            showConfigureMessageStep()
        }
        radiusBar.setOnSeekBarChangeListener(radiusBarChangeListener)
        updateRadiusWithProgress(radiusBar.progress)
        map?.animateCamera(CameraUpdateFactory.zoomTo(15f))
        showGeofenceUpdate()
    }

    private fun FragmentProfileBinding.showConfigureMessageStep() {
        marker.isGone = true
        instructionTitle.isVisible = true
        instructionSubtitle.isGone = true
        radiusBar.isGone = true
        radiusDescription.isGone = true
        message.isVisible = true
        instructionTitle.text = getString(R.string.instruction_message_description)
        next.setOnClickListener {
            hideKeyboard(requireContext(), message)
            geofence.message = message.text.toString()

            if (geofence.message.isNullOrEmpty()) {
                message.error = getString(R.string.error_required)
            } else {
                addGeofence(geofence)
            }
        }
        message.requestFocusWithKeyboard()
        showGeofenceUpdate()
    }

    private fun getRadius(progress: Int) = 100 + (2 * progress.toDouble() + 1) * 100

    private val radiusBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            binding.updateRadiusWithProgress(progress)
            showGeofenceUpdate()
        }
    }

    private fun FragmentProfileBinding.updateRadiusWithProgress(progress: Int) {
        val radius = getRadius(progress)
        geofence.radius = radius
        radiusDescription.text =
            getString(R.string.radius_description, radius.roundToInt().toString())
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val geofence = Geofencer(requireContext()).get(marker.tag as String)
        if (geofence != null) {
            binding.showGeofenceRemoveAlert(geofence)
        }
        return true
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(preferenceChangedListener)
        map = null
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        p0.uiSettings.isMyLocationButtonEnabled = false
        p0.isMyLocationEnabled=true
        p0.uiSettings.isMapToolbarEnabled = false
        p0.uiSettings.isZoomControlsEnabled = false
        p0.setOnMarkerClickListener(this@ProfileFragment)
        checkNotificationPermission()
    }
}