package com.manoj.clean.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.manoj.domain.util.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SingleRequestStateFlow<T> {

    private val stateFlow = MutableStateFlow<State<T>?>(null)

    interface Collector<T> {
        fun onRequestReceived(resource: State<T>)
    }

    fun collect(owner: LifecycleOwner, collector: Collector<T>) {
        owner.lifecycleScope.launch {
            stateFlow.collect { resource ->
                resource?.let {
                    collector.onRequestReceived(it)
                }
            }
        }
    }

    fun setValue(resource: State<T>) {
        stateFlow.value = resource
    }
}