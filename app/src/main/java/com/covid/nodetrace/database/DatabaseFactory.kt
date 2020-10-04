package com.covid.nodetrace.database

/**
 * DatabaseFactory implements the Singleton pattern for remote server database access
 */
object DatabaseFactory {
    @get:Synchronized
    private var firebaseDao: FirebaseDao? = null

    fun getFirebaseDatabase () : FirebaseDao {
        if (firebaseDao == null)
            return FirebaseDatabase()
        else {
            return firebaseDao!!
        }
    }
}