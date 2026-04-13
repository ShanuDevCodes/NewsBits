package com.shanudevcodes.newsbits.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppListUIViewModel @Inject constructor() : ViewModel() {
    private val _position = MutableStateFlow(0)
    val position = _position.asStateFlow()

    fun updatePosition(position: Int) {
        _position.value = position
    }
}