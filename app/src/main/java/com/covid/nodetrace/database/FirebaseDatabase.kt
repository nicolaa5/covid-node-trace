package com.covid.nodetrace.database

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseDatabase () : FirebaseDao {
    private val TAG: String = FirebaseDatabase::class.java.getSimpleName()

    val db = FirebaseFirestore.getInstance()
    private val contactsCollection: CollectionReference

    init {
        contactsCollection = db.collection("contacts");
    }

    override suspend fun create(context: Context, contactID: String) : Boolean = suspendCancellableCoroutine { continuation ->
        if (NetworkHelper.isConnectedToNetwork(context)) {
            val newID: Map<String, String> = mapOf(Pair("id", contactID))
            val IDs: DocumentReference = contactsCollection.document("IDs")
            IDs.set(newID)
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { failureReason ->
                    continuation.cancel()
                    Log.e(TAG, failureReason.message ?: failureReason.toString())
                }
                .addOnCanceledListener {
                    continuation.cancel()
                    Log.e(TAG, "Upload cancelled")
                }
        }
        else {
            continuation.cancel()
        }
    }

    override suspend fun read(context: Context) : List<String> = suspendCancellableCoroutine { continuation ->
        val IDs: DocumentReference = contactsCollection.document("IDs")

        if (NetworkHelper.isConnectedToNetwork(context)) {
            IDs.get()
                .addOnSuccessListener { idList ->
                    val contactIDList : List<String> = idList.data?.map { data -> data.value as String } as List<String>
                    continuation.resume(contactIDList)
                }
                .addOnFailureListener { failureReason ->
                    continuation.cancel()
                    Log.e(TAG, failureReason.message ?: failureReason.toString())
                }
                .addOnCanceledListener {
                    continuation.cancel()
                    Log.e(TAG, "Upload cancelled")
                }
        }
        else {
            // Since we're not connected to the database we get the data from the cache
            val source = Source.CACHE

            IDs.get(source)
                .addOnSuccessListener { idList ->
                    val contactIDList : List<String> = idList.data?.map { data -> data.value as String } as List<String>
                    continuation.resume(contactIDList)
                }
                .addOnFailureListener { failureReason ->
                    continuation.cancel()
                    Log.e(TAG, failureReason.message ?: failureReason.toString())
                }
                .addOnCanceledListener {
                    continuation.cancel()
                    Log.e(TAG, "Upload cancelled")
                }
        }
    }

    override suspend fun update(context: Context, contactID: String) : Boolean{
        TODO("Not yet implemented")
    }

    override suspend fun delete(context: Context, contactID: String) : Boolean {
        TODO("Not yet implemented")
    }
}