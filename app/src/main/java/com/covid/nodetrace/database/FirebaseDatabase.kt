package com.covid.nodetrace.database

import android.content.Context
import android.util.Log
import com.covid.nodetrace.Contact
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseDatabase () : FirebaseDao {
    private val TAG: String = FirebaseDatabase::class.java.getSimpleName()

    val db = FirebaseFirestore.getInstance()
    private val contactsCollection: CollectionReference

    init {
        contactsCollection = db.collection("contacts");
    }

    override suspend fun create(contactID : String) : Boolean = suspendCancellableCoroutine { continuation ->

        val contactEntry : Map<String, String> = mapOf(Pair("id", contactID))
        val newContactEntry: DocumentReference = contactsCollection.document("IDs")
        newContactEntry.set(contactEntry)
            .addOnSuccessListener { continuation.resume(true)}
            .addOnFailureListener { failureReason ->
                continuation.resume(false)
                Log.e(TAG, failureReason.message ?: failureReason.toString())
            }
            .addOnCanceledListener {
                continuation.resume(false)
                Log.e(TAG, "Upload cancelled")
            }
    }

    override suspend fun read(contactID: String) : Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun update(contactID: String) : Boolean{
        TODO("Not yet implemented")
    }

    override suspend fun delete(contactID: String) : Boolean {
        TODO("Not yet implemented")
    }
}