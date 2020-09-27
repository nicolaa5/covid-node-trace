package com.covid.nodetrace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * The app's main activity keeps track of the different screens in the forms of multiple [Fragments]
 * It also initiates the background service that is actively scanning for / advertising to nearby devices.
 */
class MainActivity : AppCompatActivity(), CoroutineScope {
    private val TAG: String = MainActivity::class.java.getSimpleName()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var auth: FirebaseAuth


    enum class Screens {
        WELCOME,
        HEALTH_STATUS,
        CONTACT,
        SETTINGS
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        authenticateUser(auth)

        startService(Intent(this, ContactService::class.java))
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
        when (item.itemId) {
            R.id.welcome_menu -> {
                showScreen(Screens.WELCOME)
            }
            R.id.health_status_menu -> {
                showScreen(Screens.HEALTH_STATUS)
            }
            R.id.contact_menu -> {
                showScreen(Screens.CONTACT)
            }
            R.id.settings_menu -> {
                showScreen(Screens.SETTINGS)
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        coroutineContext[Job]!!.cancel()
    }

    private fun authenticateUser(firebaseAuth : FirebaseAuth) {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInAnonymously:success")
                val user = auth.currentUser
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.exception)
            }
        }
    }

    fun showScreen(screen :Screens) {

        with(getPreferences(Context.MODE_PRIVATE).edit()) {
            putInt(resources.getString(R.string.screen_state), screen.ordinal)
            apply()
        }

        when (screen) {
            Screens.WELCOME -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.welcome_fragment)
            }
            Screens.HEALTH_STATUS -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.health_status_fragment)
            }
            Screens.CONTACT -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.contact_fragment)
            }
            Screens.SETTINGS -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.settings_fragment)
            }
        }
    }
}