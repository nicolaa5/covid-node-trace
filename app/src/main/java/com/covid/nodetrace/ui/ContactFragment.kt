package com.covid.nodetrace.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.covid.nodetrace.Contact
import com.covid.nodetrace.ContactHistoryAdapter
import com.covid.nodetrace.R
import com.covid.nodetrace.database.DatabaseFactory
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers


/**
 * The contact screen of the app indicates all the contacts that the user has had with people that
 * were in the same vicinity and time period as them. It contains a list of encounters the user has had in the past
 */
class ContactFragment : Fragment(), OnMapReadyCallback {
    private val model: AppViewModel by activityViewModels()

    private lateinit var contactHistoryListView : ListView
    private lateinit var contactHistoryAdapter : ContactHistoryAdapter

    private lateinit var bottom_sheet : NestedScrollView
    private lateinit var sheetBehavior : BottomSheetBehavior<NestedScrollView>
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mMap: MapView
    private lateinit var latestContactDate : TextView
    private lateinit var latestContactDuration : TextView
    private lateinit var latestContactDistance : TextView
    private lateinit var mapCardView : CardView
    private var mContacts : List<Contact> = emptyList()

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
        mapCardView = view.findViewById(R.id.contact_map_card) as CardView

        initializeBottomSheet()
        initializeMap(savedInstanceState)

        listenForContactUpdates()
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
        mMap.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMap.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    private fun initializeBottomSheet() {
        contactHistoryAdapter = ContactHistoryAdapter(requireActivity())
        contactHistoryListView.adapter = contactHistoryAdapter
        requireActivity().registerForContextMenu(contactHistoryListView)

        bottom_sheet = requireActivity().findViewById(R.id.bottom_sheet)
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        //Sheet
        bottom_sheet.setOnClickListener {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        sheetBehavior.setHideable(false);
        sheetBehavior.setPeekHeight(300);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        contactHistoryListView.setOnItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long ->
            view.setSelected(true)
            val contact : Contact = contactHistoryAdapter.getItem(position)
            displayContactFromList(contact)
        }
    }

    private fun initializeMap (savedInstanceState : Bundle?) {
        mMap = view?.findViewById(R.id.contact_map) as MapView
        mMap?.onCreate(savedInstanceState)
        mMap?.getMapAsync(this)
        mapCardView?.setVisibility(View.INVISIBLE)
    }

    private fun listenForContactUpdates() {
        model.contacts.observe(
            requireActivity(),
            androidx.lifecycle.Observer<List<Contact>> { contacts ->
                mContacts = contacts
                contactHistoryAdapter.updateValues(contacts)
                displayLatestContactFromDatabase(contacts)
            }
        )
    }

    private fun displayContactFromList(contact : Contact) {
        latestContactDate.text = "Date: " + DataFormatter.createDateFormat(contact.date)
        latestContactDuration.text = "Duration: " + DataFormatter.createDurationFormat(contact.duration)
        latestContactDistance.text = "Distance: " + DataFormatter.createDistanceFormat(contact.distance)

        if (isLocationValid(contact.latitude, contact.longitude)) {
            moveCamera(contact.latitude, contact.longitude)
        }
    }

    private fun displayLatestContactFromDatabase(contacts: List<Contact>) {
        if (isContactListEmpty())
            return

        val latestContact : Contact = getLatestContact(contacts)
        latestContactDate.text = "Date: " + DataFormatter.createDateFormat(latestContact.date)
        latestContactDuration.text = "Duration: " + DataFormatter.createDurationFormat(latestContact.duration)
        latestContactDistance.text = "Distance: " + DataFormatter.createDistanceFormat(latestContact.distance)

        getLatestContactWithValidLocation(contacts) {latestValidLocationContact ->
            mapCardView.setVisibility(View.VISIBLE)
            moveCamera(latestValidLocationContact?.latitude, latestValidLocationContact?.longitude)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.setMinZoomPreference(10f)


        if (mContacts.size > 0) {
            addMarkers(mContacts)
            val latestContact = getLatestContact(mContacts)
            moveCamera(latestContact.latitude, latestContact.longitude)
        }
    }

    private fun addMarkers (contacts: List<Contact>) {
        val validLocationContacts = filterValidLocations(contacts)
        for (contact in validLocationContacts) {
            val location = LatLng(contact.latitude, contact.longitude)
            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(DataFormatter.createDateFormat(contact.date))
            )
        }
    }

    private fun moveCamera(latitude : Double, longitude : Double) {
        if (!isLocationValid(latitude, longitude))
            return

        val location = LatLng(latitude, longitude)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    private fun getLatestContact(contacts: List<Contact>) : Contact {
        var latestDate : Long = Long.MIN_VALUE
        var latestContact : Contact = contacts.first()
        contacts.forEach{ contact ->
            if (contact.date > latestDate){
                latestDate = contact.date
                latestContact = contact
            }
        }
        return latestContact
    }

    private fun getLatestContactWithValidLocation(contacts: List<Contact>, latestValidLocationContact : (Contact) -> Unit) {
        var latestDate : Long = Long.MIN_VALUE
        var latestContact : Contact? = null
        contacts.forEach{ contact ->
            if (contact.date > latestDate && contact.latitude != 0.0 && contact.longitude != 0.0){
                latestDate = contact.date
                latestContact = contact
            }
        }
        if (latestContact != null)
            latestValidLocationContact.invoke(latestContact!!)
    }

    private fun isLocationValid (latitude : Double, longitude : Double) : Boolean {
        return latitude != 0.0 && longitude != 0.0
    }

    private fun filterValidLocations (contacts: List<Contact>) : List<Contact> {
        return contacts.filter { contact -> contact.latitude != 0.0 && contact.longitude != 0.0 }
    }

    private fun isContactListEmpty() : Boolean {
        return mContacts.size == 0
    }
}