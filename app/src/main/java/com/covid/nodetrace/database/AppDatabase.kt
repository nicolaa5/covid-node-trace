package com.covid.nodetrace.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.covid.nodetrace.Contact
import com.covid.nodetrace.util.SingletonHolder

/**
 * A single point of entry (see Singleton pattern) where access to the database can be requested
 */
@Database(entities = arrayOf(Contact::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext,
            AppDatabase::class.java, "contact-database")
            .build()
    })
}