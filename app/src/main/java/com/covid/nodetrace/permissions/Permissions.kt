package com.covid.nodetrace.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * A static class to help requesting permissions for Android
 */
object Permissions  {

    public val requiredPermissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val PERMISSION_REQUEST_CODE =  0x78

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission : String in permissions) {
            val result: Int = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    fun requestPermission(activity: Activity, permissions: Array<String>,receivedPermissions: (Boolean) -> Unit) {

        val permissionHelper = PermissionHelper(permissions)
        permissionHelper.requestPermission(activity, object : PermissionResult {
            override fun OnPermissionGranted(permission: String?, requestCode: Int) {
                receivedPermissions.invoke(true)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun OnPermissionDenied(permission: String?, requestCode: Int) {
                activity.requestPermissions(
                    permissions,
                    PERMISSION_REQUEST_CODE
                )
            }

            override fun OnShowRationale(permission: String?, requestCode: Int) {
                val permissionRationale = PermissionRationale()
                permissionRationale.showRationale(activity, permission, requestCode)
            }

            override fun OnInvalidPermissions() {
                receivedPermissions.invoke(false)
            }
        })
    }
}