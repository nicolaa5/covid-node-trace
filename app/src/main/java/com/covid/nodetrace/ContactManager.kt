package com.covid.nodetrace

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.covid.nodetrace.ContactService.Companion.DISTANCE_UPDATED
import com.covid.nodetrace.ContactService.Companion.NODE_FOUND
import com.covid.nodetrace.ContactService.Companion.NODE_LOST
import com.covid.nodetrace.database.AppDatabase
import com.covid.nodetrace.database.DatabaseFactory
import com.covid.nodetrace.permissions.Permissions
import com.covid.nodetrace.ui.AppViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet
import kotlin.coroutines.CoroutineContext

class ContactManager(context: Context, lifecycle: Lifecycle, viewModel: AppViewModel) : LifecycleObserver, CoroutineScope {
    private val TAG = "ContactManager"

    private val model : AppViewModel = viewModel
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var appDatabase : AppDatabase
    private var mContext : Context? = context
    private lateinit var contacts : HashSet<Contact>
    private lateinit var mDataBroadcastReceiver: BroadcastReceiver



    init {
        lifecycle.addObserver(this)
        initializeBroadcastReceiver()
        LocalBroadcastManager.getInstance(context).registerReceiver(
            mDataBroadcastReceiver,
            makeBroadcastFilter()
        )
        contacts = HashSet<Contact>()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart () {
        updateUserInterfaceWithContactHistory()
    }

    /**
     * Broadcast receiver that receives data from the background service.
     * Must be initialized before registering the LocalBroadcastManager receiver
     */
    private fun initializeBroadcastReceiver () {
        mDataBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    NODE_FOUND -> {
                        val foundID: String? = intent.getStringExtra("FOUND_ID")

                        if (foundID == null)
                            return

                        val contact = createNewContact(foundID)
                        contacts?.add(contact)
                    }
                    DISTANCE_UPDATED -> {
                        val ID = intent.getStringExtra("ID")
                        val distance: Double = intent.getDoubleExtra("DISTANCE", -1.0)

                        if (distance == -1.0)
                            return

                        updateContactDistance(ID, distance)
                    }
                    NODE_LOST -> {
                        val lostID = intent.getStringExtra("LOST_ID")
                        val contact: Contact? = updateContactDuration(lostID, getCurrentUnixDate())

                        //If contact can't be found we do not insert it into the database
                        if (contact == null)
                            return

                        insertContact(contact)
                        updateUserInterfaceWithContactHistory()
                    }
                }
            }
        }
    }

    /**
     * Fetches all the found contacts from the local database and displays them within the
     * Contact page of the app
     */
    fun updateUserInterfaceWithContactHistory () {
        this.launch(Dispatchers.IO) {
            val allContacts : List<Contact> = appDatabase.contactDao().getAll()

            this.launch(Dispatchers.Main) {
                model.contacts.value = allContacts
            }
        }
    }

    fun createDatabase(activity: Activity) {
        this.launch(Dispatchers.IO) {
            appDatabase = AppDatabase.getInstance(activity)
            val allContacts : List<Contact> = appDatabase.contactDao().getAll()
            allContacts.forEach { contact ->
                println(
                    "Contact -> " +
                            " ID: ${contact.ID}" +
                            " date: ${contact.date}" +
                            " duration: " + "${contact.duration / 1000f} sec" +
                            " distance: " + "${contact.distance} meter" +
                            " location: " + " {lat: ${contact.latitude}" + "," + " long: ${contact.longitude}}"
                )
            }
        }
    }


    fun getContactIDsFromDatabase () {

        this.launch(Dispatchers.Default) {
            if (mContext == null)
                return@launch

            val contactIDsRetrieved = DatabaseFactory.getFirebaseDatabase().read(mContext!!)

            compareDatabaseIDsToLocalIDs(contactIDsRetrieved)
        }
    }

    suspend fun compareDatabaseIDsToLocalIDs (databaseContactIDs : List<String>) {
        withContext(Dispatchers.IO) {
            val localContacts : List<Contact> = appDatabase.contactDao().getAll()
            val localContactIDs : List<String> = localContacts.map { contact -> contact.ID }

            val riskContactIDs = localContactIDs.filter { localContact -> databaseContactIDs.contains(localContact) }
            updateContactRiskLevel(riskContactIDs)
        }
    }

    suspend fun updateContactRiskLevel (riskContactIDs : List<String>) {
        withContext(Dispatchers.IO){
            riskContactIDs.forEach{ contactID ->
                appDatabase.contactDao().updateHealthStatus(contactID, HealthStatus.SICK)
            }
        }
    }

    fun createNewContact(ID: String) : Contact {
        val date = getCurrentUnixDate()
        val location : Location? = getCurrentLocation()

        if (location != null)
            return Contact(ID, date, location.latitude, location.longitude)
        else {
            return Contact(ID, date)
        }
    }

    fun updateContactDistance(ID: String, distance: Double) {
        for (contact in contacts) {
            if (contact.ID == ID) {
                if (distance < contact.distance)
                    contact.distance = distance
            }
        }
    }

    fun updateContactDuration(ID: String, contactEnd: Long) : Contact? {
        for (contact in contacts) {
            if (contact.ID == ID) {
                contact.duration = contactEnd - contact.date
                return contact
            }
        }
        return null
    }

    fun insertContact(contact: Contact) {
        this.launch(Dispatchers.IO) {
            appDatabase.contactDao().insert(contact)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (mContext != null)
            LocalBroadcastManager.getInstance(mContext!!).unregisterReceiver(mDataBroadcastReceiver)

        coroutineContext[Job]!!.cancel()
        mContext = null
    }

    private fun makeBroadcastFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ContactService.NODE_FOUND)
        intentFilter.addAction(ContactService.NODE_LOST)
        intentFilter.addAction(ContactService.DISTANCE_UPDATED)
        return intentFilter
    }

    fun getCurrentUnixDate() : Long {
        return System.currentTimeMillis()
    }

    fun getCurrentLocation() : Location? {
        //If context is not available then stop execution
        if (mContext == null)
            return null

        //If location permission has not been granted have to stop as we can't request permission while the app is in the background
        if (!Permissions.hasPermissions(
                mContext!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )) {
            return null
        }

        val lastKnownLocation: Location? = getLastKnownLocation()

        //Lastly if the GPS can't retrieve the location values we stop execution
        if (lastKnownLocation == null) {
            Log.e(TAG, "Can't retrieve location")
            return null
        }

        return lastKnownLocation
    }

    /**
     * This function checks for the last known location of the user
     * Using a custom permission checker therefore we suppress the "MissingPermission" flag
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation () : Location? {
        val locationManager : LocationManager = mContext?.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val lastKnownLocation: Location = locationManager.getLastKnownLocation(provider)
                ?: continue
            if (bestLocation == null || lastKnownLocation.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = lastKnownLocation
            }
        }
        return bestLocation
    }
}

