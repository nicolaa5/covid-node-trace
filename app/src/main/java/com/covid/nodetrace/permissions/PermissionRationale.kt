package com.covid.nodetrace.permissions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.covid.nodetrace.R

class PermissionRationale {
    private var rationaleTitle = 0
    private var rationaleText = 0
    fun showRationale(applicationActivity: Activity, permission: String?, requestCode: Int) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                rationaleTitle = R.string.fine_location_rationale_title
                rationaleText = R.string.fine_location_rationale_text
            }
            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                rationaleTitle = R.string.storage_rationale_title
                rationaleText = R.string.storage_rationale_text
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                rationaleTitle = R.string.storage_rationale_title
                rationaleText = R.string.storage_rationale_text
            }
            else -> return
        }
        val handler = Handler(applicationActivity.mainLooper)
        handler.post {
            AlertDialog.Builder(applicationActivity)
                .setTitle(rationaleTitle)
                .setMessage(rationaleText)
                .setNegativeButton(R.string.permission_rationale_negative) { dialog, item -> dialog.cancel() }
                .setCancelable(true)
                .setPositiveButton(
                    R.string.permission_rationale_positive,
                    DialogInterface.OnClickListener { dialogInterface, i -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            applicationActivity,
                            arrayOf(permission),
                            requestCode
                        )
                    })
                .create()
                .show()
        }
    }
}