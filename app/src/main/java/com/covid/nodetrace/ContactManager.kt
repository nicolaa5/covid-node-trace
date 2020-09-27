package com.covid.nodetrace

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.covid.nodetrace.ContactService.Companion.BROADCAST_NODE_FOUND
import com.covid.nodetrace.ContactService.Companion.BROADCAST_NODE_LOST
import com.covid.nodetrace.database.AppDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ContactManager(context: Context, lifecycle: Lifecycle) : LifecycleObserver, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var appDatabase : AppDatabase
    private var mContext : Context? = context

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

}

