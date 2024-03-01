package com.manoj.clean.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
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
import com.manoj.clean.util.geofence.GeofenceData
import com.manoj.clean.util.geofence.GeofenceRepository
import com.manoj.clean.util.geofence.hideKeyboard
import com.manoj.clean.util.geofence.isSdkVersionGreaterThanOrEqualTo
import com.manoj.clean.util.geofence.requestFocusWithKeyboard
import com.manoj.clean.util.geofence.showGeofenceInMap
import com.manoj.clean.util.getLocationPermissions
import com.manoj.clean.util.hide
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.show
import com.manoj.clean.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.container
import kotlinx.android.synthetic.main.fragment_profile.newGeofence
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {
    private val viewModel: ProfileViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var locationManager: LocationManager
    private var reminder = GeofenceData(latLng = null, radius = null, message = null)

    private val radiusBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updateRadiusWithProgress(progress)
            showReminderUpdate()
        }
    }

    private fun updateRadiusWithProgress(progress: Int) {
        val radius = getRadius(progress)
        reminder.radius = radius
        binding.radiusDescription.text =
            getString(R.string.radius_description, radius.roundToInt().toString())
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var geofenceRepository: GeofenceRepository
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

    private fun setupViews() {
        binding.apply {
            newGeofence.visibility = View.GONE

            newGeofence.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) runWithPermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION) {

                    showConfigureLocationStep()
                }
                else {

                    showConfigureLocationStep()
                }
            }
        }
        setMap()
    }


    private fun setMap() = runWithPermissions(*getLocationPermissions()) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@ProfileFragment)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        handlePermission()
    }

    private fun onMapAndPermissionReady() {
        googleMap?.let { map ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                map.isMyLocationEnabled = true
                binding.newGeofence.visibility = View.VISIBLE
                showGeoFences()
                goToMyPosition()
            }
        }
    }

    private fun handlePermission() = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        onMapAndPermissionReady()
    }


    private fun goToMyPosition() {
        val location = getLastKnownLocation()
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    private fun showGeoFences() {
        googleMap?.run {
            clear()
            for (geofence in geofenceRepository.getAll()) {
                showGeofenceInMap(requireContext(), this, geofence)
            }
        }
    }

    private fun showRemoveGeofenceAlert(geofenceData: GeofenceData) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.run {
            setMessage(getString(R.string.geofence_removal_alert))
            setButton(
                AlertDialog.BUTTON_POSITIVE, getString(R.string.geofence_removal_alert_positive)
            ) { dialog, _ ->
                removeGeofence(geofenceData)
                dialog.dismiss()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, getString(R.string.geofence_removal_alert_negative)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun removeGeofence(geofenceData: GeofenceData) {
        geofenceRepository.removeGeofence(geofenceData, success = {
            showGeoFences()
            Snackbar.make(
                binding.main, R.string.geofence_removed_success, Snackbar.LENGTH_LONG
            ).show()
        }, failure = {
            Snackbar.make(binding.main, it, Snackbar.LENGTH_LONG).show()
        })
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

    private fun showConfigureLocationStep() {
        binding.apply {
            newGeofence.hide()
            container.show()
            marker.visibility = View.VISIBLE
            instructionTitle.visibility = View.VISIBLE
            instructionSubtitle.visibility = View.VISIBLE
            radiusBar.visibility = View.GONE
            radiusDescription.visibility = View.GONE
            message.visibility = View.GONE
            instructionTitle.text = getString(R.string.instruction_where_description)
            next.setOnClickListener {
                reminder.latLng = googleMap?.cameraPosition?.target
                showConfigureRadiusStep()
            }
        }
        showReminderUpdate()
    }

    private fun showConfigureRadiusStep() {
        binding.apply {
            marker.visibility = View.GONE
            instructionTitle.visibility = View.VISIBLE
            instructionSubtitle.visibility = View.GONE
            radiusBar.visibility = View.VISIBLE
            radiusDescription.visibility = View.VISIBLE
            message.visibility = View.GONE
            instructionTitle.text = getString(R.string.instruction_radius_description)
            next.setOnClickListener {
                showConfigureMessageStep()
            }
            radiusBar.setOnSeekBarChangeListener(radiusBarChangeListener)
            updateRadiusWithProgress(radiusBar.progress)
        }
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(15f))
        showReminderUpdate()
    }

    private fun getRadius(progress: Int) = 100 + (2 * progress.toDouble() + 1) * 100

    private fun showConfigureMessageStep() {
        binding.apply {
            marker.visibility = View.GONE
            instructionTitle.visibility = View.VISIBLE
            instructionSubtitle.visibility = View.GONE
            radiusBar.visibility = View.GONE
            radiusDescription.visibility = View.GONE
            message.visibility = View.VISIBLE
            instructionTitle.text = getString(R.string.instruction_message_description)
            next.setOnClickListener {
                hideKeyboard(requireContext(), message)

                reminder.message = message.text.toString()

                if (reminder.message.isNullOrEmpty()) message.error =
                    getString(R.string.error_required)
                else addReminderWithPermission(reminder)
            }
            message.requestFocusWithKeyboard()
        }
        showReminderUpdate()
    }

    private fun addReminder(reminder: GeofenceData) {
        geofenceRepository.addGeofence(reminder,
            success = {
                binding.container.hide()
                newGeofence.show()
            },
            failure = {
                Snackbar.make(binding.main, it, Snackbar.LENGTH_LONG).show()
            })
    }

    private fun addReminderWithPermission(reminder: GeofenceData) {
        if (isSdkVersionGreaterThanOrEqualTo(Build.VERSION_CODES.TIRAMISU)) {
            runWithPermissions(Manifest.permission.POST_NOTIFICATIONS) {
                addReminder(reminder)
            }
        } else addReminder(reminder)
    }

    private fun showReminderUpdate() {
        googleMap?.clear()
        googleMap?.let { showGeofenceInMap(requireContext(), it, reminder) }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val geofence = geofenceRepository.get(marker.tag as String)
        geofence?.let {
            showRemoveGeofenceAlert(geofence)
        }
        return true
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        this.googleMap?.run {
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isMapToolbarEnabled = false
            //uiSettings.isZoomGesturesEnabled = true
            //  uiSettings.isZoomControlsEnabled = true
            uiSettings.setAllGesturesEnabled(true)
            setOnMarkerClickListener(this@ProfileFragment)
        }
        handlePermission()
    }

}