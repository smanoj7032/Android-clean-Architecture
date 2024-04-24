package com.manoj.clean.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
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
import com.manoj.clean.ui.common.base.common.permissionutils.QuickPermissionsOptions
import com.manoj.clean.ui.common.base.common.permissionutils.runWithPermissions
import com.manoj.clean.ui.common.customdialogs.CustomDialog
import com.manoj.clean.ui.common.customdialogs.DialogStyle
import com.manoj.clean.ui.common.customdialogs.DialogType
import com.manoj.clean.ui.common.customdialogs.OnDialogClickListener
import com.manoj.clean.ui.geofence.GeofenceActivity
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.geofence.GeofenceData
import com.manoj.clean.util.geofence.GeofenceRepository
import com.manoj.clean.util.geofence.showGeofenceInMap
import com.manoj.clean.util.getLocationPermissions
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(), GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {
    private val viewModel: ProfileViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private lateinit var locationManager: LocationManager


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
        observeViewModel()
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            newGeofence.visibility = View.GONE
            newGeofence.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) runWithPermissions(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    options = QuickPermissionsOptions(handleRationale = true)
                ) {
                    startGeofenceActivity()
                }
                else startGeofenceActivity()

            }
        }
        setMap()
    }

    private fun startGeofenceActivity() {
        googleMap?.run {
            val intent = GeofenceActivity.newIntent(
                requireContext(), cameraPosition.target, cameraPosition.zoom
            )
            addGeoFenceActivityLauncher.launch(
                intent,
                ActivityOptionsCompat.makeCustomAnimation(
                    requireContext(),
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            )
        }
    }


    private fun setMap() = runWithPermissions(
        *getLocationPermissions(),
        options = QuickPermissionsOptions(handleRationale = true)
    ) {
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
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        options = QuickPermissionsOptions(handleRationale = true)
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
        CustomDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setMessage(getString(R.string.geofence_removal_alert))
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: CustomDialog.Builder) {
                    removeGeofence(geofenceData)
                    dialog.dismiss()
                }

                override fun onNegativeClick(dialog: CustomDialog.Builder) {
                    dialog.dismiss()
                }
            }).show()
    }

    private fun removeGeofence(geofenceData: GeofenceData) {
        geofenceRepository.removeGeofence(geofenceData, success = {
            showGeoFences()
            CustomDialog.Builder(requireActivity(), DialogStyle.TOASTER, DialogType.SUCCESS)
                .setTitle("Alert").setMessage("Geofence removed!").show()
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

    private val addGeoFenceActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                showGeoFences()

                val reminder = geofenceRepository.getLast()
                reminder?.latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                    ?.let { googleMap?.moveCamera(it) }

                Snackbar.make(binding.main, R.string.geofence_added_success, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
}