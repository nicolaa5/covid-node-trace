package com.covid.nodetrace.database

import android.content.Context
import com.covid.nodetrace.Contact

interface FirebaseDao {
    suspend fun create(contactID : String) : Boolean
    suspend fun read(contactID: String) : Boolean
    suspend fun update(contactID: String) : Boolean
    suspend fun delete(contactID: String) : Boolean
}