package com.covid.nodetrace.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.covid.nodetrace.R

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HealthStatusFragment : Fragment() {

    private val statuses = arrayOf("Healthy", "Diagnosed with COVID-19")
    private val statusDescription = arrayOf("I am healthy", "I have been diagnosed with COVID-19")
    private lateinit var healthStatus: NumberPicker
    private lateinit var updateHealthStatusButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_status_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        updateHealthStatusButton = view.findViewById(R.id.update_health_status_button)
        healthStatus = view.findViewById(R.id.health_status_picker)

        healthStatus?.setMinValue(0)
        healthStatus?.setMaxValue(statuses.size - 1)
        healthStatus?.setDisplayedValues(statusDescription)

        val healthStatusFromStorage: Int = sharedPref.getInt(getString(R.string.health_status), 0)
        checkButtonState(healthStatusFromStorage)

        healthStatus.setOnValueChangedListener { picker, oldVal, newVal ->
            val newStatus = statuses[newVal]
            val currentStatus: Int = sharedPref.getInt(getString(R.string.health_status), 0)

            checkButtonState(currentStatus)
        }

    }

    fun checkButtonState (currentStatus : Int) {
        if (currentStatus == (healthStatus?.getValue() ?: 0)) {
            updateHealthStatusButton.setEnabled(false)
        }
        else {
            updateHealthStatusButton.setEnabled(true)
        }
    }
}