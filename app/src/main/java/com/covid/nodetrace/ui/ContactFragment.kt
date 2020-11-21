package com.covid.nodetrace.ui

import android.graphics.Color
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
import com.covid.nodetrace.HealthStatus
import com.covid.nodetrace.R
import com.covid.nodetrace.util.DataFormatter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior


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

    private lateinit var healthStatusTitle : TextView
    private lateinit var dateTitle : TextView
    private lateinit var durationTitle : TextView

    private lateinit var contactHealthStatus : TextView
    private lateinit var contactDate : TextView
    private lateinit var contactDuration : TextView
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

        healthStatusTitle = view.findViewById(R.id.health_title) as TextView
        dateTitle = view.findViewById(R.id.date_title) as TextView
        durationTitle = view.findViewById(R.id.duration_title) as TextView

        contactHealthStatus = view.findViewById(R.id.contact_health_status) as TextView
        contactDate = view.findViewById(R.id.contact_date) as TextView
        contactDuration = view.findViewById(R.id.contact_duration) as TextView
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

    /**
     * Sets listeners for UI changes and initializes components
     */
    private fun initializeBottomSheet() {
        contactHistoryAdapter = ContactHistoryAdapter(requireActivity())
        contactHistoryListView.adapter = contactHistoryAdapter
        requireActivity().registerForContextMenu(contactHistoryListView)
        contactHistoryAdapter.renderList(contactHistoryListView)

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
            view.setBackgroundColor(Color.GRAY)
            val contact : Contact = contactHistoryAdapter.getItem(position)
            displayContactFromList(contact)
        }
    }

    /**
     * Initializes the map that is shown
     */
    private fun initializeMap(savedInstanceState: Bundle?) {
        mMap = view?.findViewById(R.id.contact_map) as MapView
        mMap?.onCreate(savedInstanceState)
        mMap?.getMapAsync(this)
        mapCardView?.setVisibility(View.INVISIBLE)
    }

    /**
     * Observers @see AppViewModel for new changes regarding the list of registered contacts
     */
    private fun listenForContactUpdates() {
        model.contacts.observe(
            requireActivity(),
            androidx.lifecycle.Observer<List<Contact>> { contacts ->
                mContacts = contacts
                contactHistoryAdapter.updateValues(contacts, contactHistoryListView)
                displayLatestContactFromDatabase(contacts)
            }
        )
    }

    /**
     * Displays a contact on the UI and moves the camera location of the map
     * if a valid location is found for that contact
     */
    private fun displayContactFromList(contact: Contact) {
        displayContactData(contact)

        if (isLocationValid(contact.latitude, contact.longitude)) {
            moveCamera(contact.latitude, contact.longitude)
        }
    }

    /**
     * Picks the latest contact from the local database to be displayed in the app UI
     */
    private fun displayLatestContactFromDatabase(contacts: List<Contact>) {
        if (isContactListEmpty())
            return

        val latestContact : Contact = getLatestContact(contacts)
        displayContactData(latestContact)

        getLatestContactWithValidLocation(contacts) { latestValidLocationContact ->
            mapCardView.setVisibility(View.VISIBLE)
            moveCamera(latestValidLocationContact?.latitude, latestValidLocationContact?.longitude)
        }
    }

    /**
     * Sets all the UI component data from the supplied contact
     */
    private fun displayContactData(contact : Contact) {
        healthStatusTitle.setVisibility(View.VISIBLE)
        dateTitle.setVisibility(View.VISIBLE)
        durationTitle.setVisibility(View.VISIBLE)

        var textColor = Color.GRAY
        val status : HealthStatus = HealthStatus.valueOf(contact.healthStatus)

        when (status) {
            HealthStatus.HEALTHY -> {
                textColor = Color.rgb(79, 165, 85)
            }
            HealthStatus.SICK -> {
                textColor = Color.rgb(235, 64, 52)
            }
        }

        contactHealthStatus.setTextColor(textColor)
        contactHealthStatus.text = DataFormatter.createHealthStatusFormat(status)
        contactDate.text =DataFormatter.createDateFormat(contact.date)
        contactDuration.text = DataFormatter.createDurationFormat(contact.duration)
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

    /**
     * Adds markers on the map for every supplied contact
     */
    private fun addMarkers(contacts: List<Contact>) {
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

    /**
     * Moves the camera of the map if the location is valid
     */
    private fun moveCamera(latitude: Double, longitude: Double) {
        if (!isLocationValid(latitude, longitude))
            return

        val location = LatLng(latitude, longitude)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    /**
     * Gets the latest known contact from a list of contact
     * The latest known contact is the one with the most recent recorded date
     */
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

    /**
     * Gets all contacts that have valid latitude and longitude
     */
    private fun getLatestContactWithValidLocation(
        contacts: List<Contact>,
        latestValidLocationContact: (Contact) -> Unit
    ) {
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

    /**
     * Checks if the latitude and longitude are valid
     */
    private fun isLocationValid(latitude: Double, longitude: Double) : Boolean {
        return latitude != 0.0 && longitude != 0.0
    }

    /**
     * Filters all locations that are valid
     */
    private fun filterValidLocations(contacts: List<Contact>) : List<Contact> {
        return contacts.filter { contact -> contact.latitude != 0.0 && contact.longitude != 0.0 }
    }

    /**
     * @return if contact list is empty or not
     */
    private fun isContactListEmpty() : Boolean {
        return mContacts.size == 0
    }
}