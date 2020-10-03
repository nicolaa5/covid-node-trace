package com.covid.nodetrace.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.covid.nodetrace.Contact

@Database(entities = arrayOf(Contact::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}