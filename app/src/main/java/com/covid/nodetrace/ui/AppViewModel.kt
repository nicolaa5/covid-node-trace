package com.covid.nodetrace.ui

import android.bluetooth.le.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.covid.nodetrace.Contact
import com.covid.nodetrace.ContactService

/**
 * A model that keeps track of UI data that's changed by the user {@see communicationType}
 * or fetched from the database {@see contacts}
 */
class AppViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val communicationType = MutableLiveData<ContactService.CommunicationType>()
    val contacts = MutableLiveData<List<Contact>>()
    val advertisements = MutableLiveData<ScanResult>()
}