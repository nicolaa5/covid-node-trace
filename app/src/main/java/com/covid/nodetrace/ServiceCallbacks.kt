package com.covid.nodetrace

interface ServiceCallbacks {
    fun onServiceBound(binder : ContactService.LocalBinder)
    fun onServiceUnbound()
}