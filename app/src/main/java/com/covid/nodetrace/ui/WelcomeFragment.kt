package com.covid.nodetrace.ui

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.covid.nodetrace.R
import com.covid.nodetrace.permissions.PermissionHelper
import com.covid.nodetrace.permissions.PermissionRationale

/**
 * The first screen that a new user of the app sees.
 * It shares some information about the application and how to use it.
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<Button>(R.id.welcome_screen_button)

        button.setOnClickListener {
            val permissionRationale : PermissionRationale = PermissionRationale()
            permissionRationale.showRationale(requireActivity(),PermissionHelper.Companion.PERMISSION_REQUEST_CODE)
            findNavController().navigate(R.id.health_status_fragment)
        }
    }
}