package com.shanudevcodes.newsbits.data.savedarticledb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class RoomViewModelFactory(
    private val dao: RoomDao
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}