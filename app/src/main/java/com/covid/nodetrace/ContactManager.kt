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
import com.covid.nodetrace.ContactService.Companion.BROADCAST_NODE_FOUND
import com.covid.nodetrace.ContactService.Companion.BROADCAST_NODE_LOST
import com.covid.nodetrace.database.AppDatabase
import com.covid.nodetrace.permissions.Permissions
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ContactManager(context: Context, lifecycle: Lifecycle) : LifecycleObserver, CoroutineScope {
    private val TAG = "ContactManager"

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var appDatabase : AppDatabase
    private var mContext : Context? = context
    private var contactStart : Long? = null


    /**
     * Broadcast receiver that receives data from the background service.
     * Must be initialized before registering the LocalBroadcastManager receiver
     */
    private val mDataBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                BROADCAST_NODE_FOUND -> {
                    val foundID = intent.getStringExtra("FOUND_ID")
                }

                BROADCAST_NODE_LOST -> {
                    val lostID = intent.getStringExtra("LOST_ID")
                }
            }
        }
    }

    init {
        lifecycle.addObserver(this)
        LocalBroadcastManager.getInstance(context).registerReceiver(
            mDataBroadcastReceiver,
            makeBroadcastFilter()
        )

    }

    fun createDatabase(activity: Activity) {
        this.launch(Dispatchers.IO) {
            appDatabase = Room.databaseBuilder(
                activity,
                AppDatabase::class.java,
                "contact-database"
            ).build()
        }
    }

    fun insertContact(contact: Contact) {
        val date = getCurrentUnixDate()
        contactStart = date

        val location : Array<Double>? = getCurrentLocation()

        appDatabase.contactDao().insert(contact)
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
        return intentFilter
    }

    fun getCurrentUnixDate() : Long {
        return System.currentTimeMillis()
    }

    fun getCurrentLocation() : Array<Double>? {
        //If context is not available then stop execution
        if (mContext == null)
            return null

        //If location permission has not been granted have to stop as we can't request permission while the app is in the background
        if (!Permissions.hasPermissions(mContext!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
            return null
        }

        val lastKnownLocation: Location? = getLastKnownLocation()

        //Lastly if the GPS can't retrieve the location values we stop execution
        if (lastKnownLocation == null) {
            Log.e(TAG, "Can't retrieve location")
            return null
        }

        return arrayOf(lastKnownLocation.latitude, lastKnownLocation.latitude)
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

