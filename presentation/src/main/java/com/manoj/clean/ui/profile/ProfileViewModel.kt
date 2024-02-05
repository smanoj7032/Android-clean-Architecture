package com.manoj.clean.ui.profile

import com.manoj.clean.ui.common.base.BaseViewModel
import com.manoj.data.util.DispatchersProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(dispatchersProvider: DispatchersProvider) :
    BaseViewModel(dispatchersProvider) {
}