package com.covid.nodetrace

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.api.gax.paging.Page
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.common.collect.Lists
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext


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
            val file = File("/google-services.json")

            val otherfile = File("C:/Users/\'Niek Bijman\'/AppData/Roaming/gcloud/application_default_credentials.json")
            initializeBackend(file)
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

    suspend fun initializeBackend (keyFile: File) : Boolean = suspendCancellableCoroutine { continuation ->
        if (!keyFile.isFile)
            continuation.isCancelled

        val projectPath: String? = System.getenv("GCLOUD_PROJECT")
        val credentialsPath: String? = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
        val awsPath: String? = System.getenv("AWS_DEFAULT_REGION")
        val options: FirebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(keyFile))) //getApplicationDefault()) //
            .setDatabaseUrl("https://covid-node-trace.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(options)
    }

    @Throws(IOException::class)
    fun authExplicit(jsonPath: String?) {

        if (jsonPath.isNullOrEmpty())
            return

        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(FileInputStream(jsonPath))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))
        val storage: Storage =
            StorageOptions.newBuilder().setCredentials(credentials).build().service
        println("Buckets:")
        val buckets: Page<Bucket> = storage.list()
        for (bucket in buckets.iterateAll()) {
            System.out.println(bucket.toString())
        }
    }
}