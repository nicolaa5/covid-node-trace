package com.covid.nodetrace

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import com.google.common.collect.Lists
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        this.launch(Dispatchers.IO) {
            val jsonFile = resources.openRawResource(R.raw.credentials)
            initializeBackend(jsonFile)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineContext[Job]!!.cancel()
    }

    suspend fun initializeBackend(credentialsStream: InputStream?) : Boolean = suspendCancellableCoroutine { continuation ->

        if (credentialsStream == null)
            continuation.resume(false)

        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))

        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(credentials)
            .setDatabaseUrl("https://covid-node-trace.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(options)
        continuation.resume(true)
    }
}