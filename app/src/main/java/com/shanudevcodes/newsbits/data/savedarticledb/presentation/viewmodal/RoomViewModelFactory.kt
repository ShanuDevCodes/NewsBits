package com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shanudevcodes.newsbits.data.savedarticledb.data.dao.RoomDao

@Suppress("UNCHECKED_CAST")
class RoomViewModelFactory(
    private val dao: RoomDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}