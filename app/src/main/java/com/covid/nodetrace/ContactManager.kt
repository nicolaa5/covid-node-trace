package com.covid.nodetrace

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.room.Room
import com.covid.nodetrace.database.AppDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ContactManager(lifecycle: Lifecycle) : LifecycleObserver, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var appDatabase : AppDatabase

    init {
        lifecycle.addObserver(this)
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
        coroutineContext[Job]!!.cancel()
    }

}

