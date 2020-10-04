package com.covid.nodetrace.database

import android.content.Context
import com.covid.nodetrace.Contact

interface FirebaseDao {
    suspend fun create(context : Context, contact: Contact)
    suspend fun read(context : Context, contact: Contact)
    suspend fun update(context : Context, contact: Contact)
    suspend fun delete(context : Context, contact: Contact)
}