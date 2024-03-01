package com.manoj.clean.ui.geofence

import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.ui.common.base.BaseViewModel
import com.manoj.clean.util.SingleRequestStateFlow
import com.manoj.clean.util.geofence.GeofenceData
import com.manoj.clean.util.geofence.GeofenceRepository
import com.manoj.data.util.DispatchersProvider
import com.manoj.domain.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    private val geofenceRepository: GeofenceRepository
) : BaseViewModel(dispatchers) {
    val reminderFlow: SingleRequestStateFlow<Boolean> = SingleRequestStateFlow()

    fun addReminder(reminder: GeofenceData) {
        reminderFlow.setValue(State.loading())
        geofenceRepository.addGeofence(reminder,
            success = {
                reminderFlow.setValue(State.success(true))
            },
            failure = {
                reminderFlow.setValue(State.success(false))
            })
    }
}