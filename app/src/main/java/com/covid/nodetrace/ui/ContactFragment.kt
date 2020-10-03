package com.covid.nodetrace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.covid.nodetrace.Contact
import com.covid.nodetrace.ContactHistoryAdapter
import com.covid.nodetrace.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/**
 * The contact screen of the app indicates all the contacts that the user has had with people that
 * were in the same vicinity and time period as them. It contains a list of encounters the user has had in the past
 */
class ContactFragment : Fragment(), OnMapReadyCallback {
    private val model: AppViewModel by activityViewModels()

    private lateinit var contactHistoryListView : ListView
    private lateinit var contactHistoryAdapter : ContactHistoryAdapter

    private lateinit var mMap: MapView
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var latestContactDate : TextView
    private lateinit var latestContactDuration : TextView
    private lateinit var latestContactDistance : TextView

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
        latestContactDate = view.findViewById(R.id.contact_date) as TextView
        latestContactDuration = view.findViewById(R.id.contact_duration) as TextView
        latestContactDistance= view.findViewById(R.id.contact_distance) as TextView

        contactHistoryAdapter = ContactHistoryAdapter(requireActivity())
        contactHistoryListView.adapter = contactHistoryAdapter
        requireActivity().registerForContextMenu(contactHistoryListView)

        model.contacts.observe(
            requireActivity(),
            androidx.lifecycle.Observer<List<Contact>> { contacts ->
                contactHistoryAdapter.updateValues(contacts)
                displayLatestContact(contacts)
            }
        )

        val mapOptions : GoogleMapOptions = GoogleMapOptions().camera(
            CameraPosition.fromLatLngZoom(LatLng(59.0, 18.0), 100f)
        )

        mapOptions.maxZoomPreference
        mMap = view?.findViewById(R.id.contact_map) as MapView
        mMap?.onCreate(savedInstanceState)
        mMap?.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        mMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        onPause()
    }

    override fun onStop() {
        super.onStop()
        mMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    private fun displayLatestContact(contacts: List<Contact>) {
        var latestDate : Long = Long.MIN_VALUE
        var latestContact : Contact = contacts.first()
        contacts.forEach{ contact ->
            if (contact.date > latestDate){
                latestDate = contact.date
                latestContact = contact
            }
        }
        latestContactDate.text = "Date: " + DataFormatter.createDateFormat(latestContact.date)
        latestContactDuration.text = "Duration: " + DataFormatter.createDurationFormat(latestContact.duration)
        latestContactDistance.text = "Distance: " + DataFormatter.createDistanceFormat(latestContact.distance)
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
        mGoogleMap = googleMap

        // Add a marker in Stockholm and move the camera
        val stockholm = LatLng(59.331264, 18.064854)
        mGoogleMap.setMinZoomPreference(10f)
        mGoogleMap.addMarker(
            MarkerOptions()
                .position(stockholm)
                .title("Marker in Stockholm")
        )
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(stockholm))
    }
}