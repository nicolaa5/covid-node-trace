package com.covid.nodetrace.permissions

/**
 * An interface to help with sending callbacks for permission related actions
 */
interface PermissionResult {
    fun OnPermissionGranted(permission: String?, requestCode: Int)
    fun OnPermissionDenied(permission: String?, requestCode: Int)
    fun OnShowRationale(permission: String?, requestCode: Int)
    fun OnInvalidPermissions()
}