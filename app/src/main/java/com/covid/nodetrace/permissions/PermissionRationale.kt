package com.covid.nodetrace.permissions

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.covid.nodetrace.R
import com.covid.nodetrace.permissions.Permissions.requiredPermissions


class PermissionRationale {

    fun showRationale(applicationActivity: Activity, requestCode: Int) {

        val customDialog = Dialog(applicationActivity)

        val displayRectangle = Rect()
        val window: Window = applicationActivity.getWindow()
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

        val layoutInflater = LayoutInflater.from(applicationActivity)
        val dialogView = layoutInflater.inflate(R.layout.permission_dialog, null)

        val grantPermissionButton  : Button =  dialogView.findViewById(R.id.permission_button_grant)
        val denyPermissionButton  : Button =  dialogView.findViewById(R.id.permission_button_deny)

        customDialog.setContentView(dialogView)
        grantPermissionButton.setOnClickListener {
            Permissions.requestPermission(applicationActivity, requiredPermissions)
            customDialog.dismiss()
        }

        denyPermissionButton.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()

        dialogView.setMinimumWidth((displayRectangle.width() * 0.7f).toInt())
        dialogView.setMinimumHeight((displayRectangle.width() * 0.7f).toInt())
    }
}