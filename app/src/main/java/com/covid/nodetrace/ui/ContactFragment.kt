package com.covid.nodetrace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.covid.nodetrace.Contact
import com.covid.nodetrace.ContactHistoryAdapter
import com.covid.nodetrace.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Observer


/**
 * The contact screen of the app indicates all the contacts that the user has had with people that
 * were in the same vicinity and time period as them. It contains a list of encounters the user has had in the past
 */
class ContactFragment : Fragment(), OnMapReadyCallback {
    private val model: AppViewModel by activityViewModels()

    private lateinit var contactHistoryListView : ListView
    private lateinit var contactHistoryAdapter : ContactHistoryAdapter

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.contact_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactHistoryListView = view.findViewById(R.id.contact_history_list_view) as ListView

        contactHistoryAdapter = ContactHistoryAdapter(requireActivity())
        contactHistoryListView.adapter = contactHistoryAdapter
        requireActivity().registerForContextMenu(contactHistoryListView)

        model.contacts.observe(requireActivity(), androidx.lifecycle.Observer<List<Contact>>  { contacts ->
            contactHistoryAdapter.updateValues(contacts)
        })
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Stockholm and move the camera
        val stockholm = LatLng(59.0, 18.0)
        mMap.addMarker(MarkerOptions()
            .position(stockholm)
            .title("Marker in Stockholm"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stockholm))
    }
}