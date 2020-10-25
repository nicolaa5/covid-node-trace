package com.covid.nodetrace.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.covid.nodetrace.ContactService
import com.covid.nodetrace.MainActivity
import com.covid.nodetrace.R

/**
 * The first screen that a new user of the app sees.
 * It shares some information about the application and how to use it.
 *
 * In the settings of the app the user can configure how the application behaves.
 */
class WelcomeFragment : Fragment() {
    private val model: AppViewModel by activityViewModels()

    private lateinit var advertiseOrScanSwitch : Switch
    private var devModeClicks : Int = 0
    private final val DEV_CLICKS_NEEDED = 6

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToAppButton = view.findViewById<Button>(R.id.welcome_screen_button)

        goToAppButton.setOnClickListener {
            findNavController().navigate(R.id.contact_fragment)

            with(requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
                putInt(resources.getString(com.covid.nodetrace.R.string.screen_state), MainActivity.Screens.CONTACT.ordinal)
                apply()
            }
        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val communicationTypeFromStorage : Int = sharedPref.getInt(getString(R.string.communication_type_state), 0)

        advertiseOrScanSwitch = view.findViewById(R.id.type_switch)

        if (communicationTypeFromStorage == 1) {
            advertiseOrScanSwitch.setChecked(false)
        }
        else {
            advertiseOrScanSwitch.setChecked(true)
        }

        advertiseOrScanSwitch.setOnCheckedChangeListener{switchView, isChecked ->

            var communicationTypeState = 0

            if(isChecked) {
                //The app only scans for devices if it's set to 'USER'
                model.communicationType.value = ContactService.CommunicationType.SCAN
                communicationTypeState = ContactService.CommunicationType.SCAN.ordinal
            }
            else {
                //The app only advertises if it's set to 'NODE'
                model.communicationType.value = ContactService.CommunicationType.ADVERTISE
                communicationTypeState = ContactService.CommunicationType.ADVERTISE.ordinal
            }

            with (requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
                putInt(resources.getString(R.string.communication_type_state), communicationTypeState)
                apply()
            }
        }

        val metrics : DisplayMetrics = DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        val width : Int = metrics.widthPixels;
        val height : Int = metrics.heightPixels;

        var imageHolder : RelativeLayout =  view.findViewById(R.id.image_holder)
        val welcomeImage = view.findViewById(R.id.welcome_screen_graphic) as ImageView

        val devMode : Boolean = sharedPref.getBoolean(getString(R.string.dev_mode), false)

        if (devMode) {
            val devSection = view.findViewById(R.id.dev_section) as LinearLayout
            devSection.visibility = View.VISIBLE

            val params : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, height/6);
            welcomeImage.setLayoutParams(params)
        }
        else {
            val devSection = view.findViewById(R.id.dev_section) as LinearLayout
            devSection.visibility = View.GONE

            val params : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, height/3);
            welcomeImage.setLayoutParams(params)
        }

        welcomeImage.setOnClickListener {
            devModeClicks++

            if (devModeClicks >= 2 && devModeClicks < DEV_CLICKS_NEEDED) {
                Toast.makeText(requireContext(), "${DEV_CLICKS_NEEDED - devModeClicks} steps away from dev mode", Toast.LENGTH_SHORT).show()
            }
            else if (devModeClicks >= DEV_CLICKS_NEEDED) {
                val devSection = view.findViewById(R.id.dev_section) as LinearLayout
                devSection.visibility = View.VISIBLE

                val params : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width, height/6);
                welcomeImage.setLayoutParams(params)

                with (requireActivity().getPreferences(Context.MODE_PRIVATE).edit()) {
                    putBoolean(resources.getString(R.string.dev_mode), true)
                    apply()
                }
            }
        }
    }
}