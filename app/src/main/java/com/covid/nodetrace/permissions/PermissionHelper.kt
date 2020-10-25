package com.covid.nodetrace.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * The PermissionHelper structure is derived from Android's suggested permission structure
 * https://developer.android.com/training/permissions/requesting#java
 */
class PermissionHelper (private val requestedPermissions: Array<String>?) {

    companion object {
        private val TAG: String = PermissionHelper::class.java.getSimpleName()
        public const val PERMISSION_REQUEST_CODE = 1
    }
    /**
     * A method to handle the response to requesting an Android permission
     * @param applicationActivity: The activity that can show a rationale
     * @param permissionResult: The callback that communicates the result of the request
     */
    fun requestPermission(applicationActivity: Activity?, permissionResult: PermissionResult) {
        if (requestedPermissions == null) {
            permissionResult.OnInvalidPermissions()
            return
        }
        for (permission in requestedPermissions) {
            if (ContextCompat.checkSelfPermission(
                    applicationActivity!!,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        applicationActivity,
                        permission
                    )
                ) {
                    permissionResult.OnShowRationale(
                        permission,
                        PermissionHelper.Companion.PERMISSION_REQUEST_CODE
                    )
                } else {
                    permissionResult.OnPermissionDenied(
                        permission,
                        PermissionHelper.Companion.PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                // Permission has already been granted
                permissionResult.OnPermissionGranted(
                    permission,
                    PermissionHelper.Companion.PERMISSION_REQUEST_CODE
                )
            }
        }
    }
}