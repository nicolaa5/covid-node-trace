package com.covid.nodetrace.permissions

interface PermissionResult {
    fun OnPermissionGranted(permission: String?, requestCode: Int)
    fun OnPermissionDenied(permission: String?, requestCode: Int)
    fun OnShowRationale(permission: String?, requestCode: Int)
    fun OnInvalidPermissions()
}