package com.covid.nodetrace.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.covid.nodetrace.Contact
import com.covid.nodetrace.ContactService


class AppViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val communicationType = MutableLiveData<ContactService.CommunicationType>()
    val contacts = MutableLiveData<List<Contact>>()
}