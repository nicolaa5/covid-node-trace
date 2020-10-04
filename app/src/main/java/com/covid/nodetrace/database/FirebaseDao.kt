package com.covid.nodetrace.database

import android.content.Context
import com.covid.nodetrace.Contact

interface FirebaseDao {
    suspend fun create(context: Context, contactID : String) : Boolean
    suspend fun read(context: Context) : List<String>
    suspend fun update(context: Context, contactID: String) : Boolean
    suspend fun delete(context: Context, contactID: String) : Boolean
}