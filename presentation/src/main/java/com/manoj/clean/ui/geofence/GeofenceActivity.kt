package com.manoj.clean.ui.geofence

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityGeofenceBinding
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.base.common.permissionutils.runWithPermissions
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.SingleRequestStateFlow
import com.manoj.clean.util.geofence.GeofenceData
import com.manoj.clean.util.geofence.hideKeyboard
import com.manoj.clean.util.geofence.isSdkVersionGreaterThanOrEqualTo
import com.manoj.clean.util.geofence.requestFocusWithKeyboard
import com.manoj.clean.util.geofence.showGeofenceInMap
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.showSnackBar
import com.manoj.domain.util.State
import com.manoj.domain.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class GeofenceActivity : BaseActivity<ActivityGeofenceBinding>(), OnMapReadyCallback {
    private val viewModel: GeofenceViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityGeofenceBinding =
        ActivityGeofenceBinding.inflate(inflater)

    private lateinit var googleMap: GoogleMap

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

    companion object {
        private const val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"
        private const val EXTRA_ZOOM = "EXTRA_ZOOM"

        fun newIntent(context: Context, latLng: LatLng, zoom: Float): Intent {
            return Intent(context, GeofenceActivity::class.java).apply {
                putExtra(EXTRA_LAT_LNG, latLng)
                putExtra(EXTRA_ZOOM, zoom)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViews()
        observeViewModel()
    }

    private fun setViews() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.apply {
            instructionTitle.visibility = View.GONE
            instructionSubtitle.visibility = View.GONE
            radiusBar.visibility = View.GONE
            radiusDescription.visibility = View.GONE
            message.visibility = View.GONE
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.apply {
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isZoomControlsEnabled = true
        }
        centerCamera()
        showConfigureLocationStep()
    }

    private fun centerCamera() {
        intent.extras?.let { extras ->
            val latLng = extras.get(EXTRA_LAT_LNG) as LatLng
            val zoom = extras.getFloat(EXTRA_ZOOM)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    private fun showConfigureLocationStep() {
        binding.apply {
            marker.visibility = View.VISIBLE
            instructionTitle.visibility = View.VISIBLE
            instructionSubtitle.visibility = View.VISIBLE
            radiusBar.visibility = View.GONE
            radiusDescription.visibility = View.GONE
            message.visibility = View.GONE
            instructionTitle.text = getString(R.string.instruction_where_description)
            next.setOnClickListener {
                reminder.latLng = googleMap.cameraPosition.target
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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
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
                hideKeyboard(this@GeofenceActivity, message)

                reminder.message = message.text.toString()

                if (reminder.message.isNullOrEmpty()) message.error =
                    getString(R.string.error_required)
                else addReminderWithPermission(reminder)
            }
            message.requestFocusWithKeyboard()
        }
        showReminderUpdate()
    }


    private fun addReminderWithPermission(reminder: GeofenceData) {
        if (isSdkVersionGreaterThanOrEqualTo(Build.VERSION_CODES.TIRAMISU)) {
            runWithPermissions(Manifest.permission.POST_NOTIFICATIONS) {
                viewModel.addReminder(reminder)
            }
        } else viewModel.addReminder(reminder)
    }

    private fun showReminderUpdate() {
        googleMap.clear()
        showGeofenceInMap(this, googleMap, reminder)
    }

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
            launch {
                reminderFlow.collect(this@GeofenceActivity,
                    object : SingleRequestStateFlow.Collector<Boolean> {
                        override fun onRequestReceived(resource: State<Boolean>) {
                            when (resource.status) {
                                Status.LOADING -> onLoading(true)
                                Status.SUCCESS -> {
                                    onLoading(false)
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }

                                else -> {
                                    onLoading(false)
                                    Snackbar.make(
                                        binding.main,
                                        "Unable to add geofence",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }
                    })
            }
        }
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
    }
}