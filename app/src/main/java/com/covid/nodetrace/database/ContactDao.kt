package com.covid.nodetrace.database

import androidx.room.*
import com.covid.nodetrace.Contact
import com.covid.nodetrace.HealthStatus

@Dao
interface ContactDao {
    /**
     * Gets all contacts stored in the local database
     */
    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    /**
     * Gets all contacts within the indicated time period.
     * The query is done with UNIX time (milliseconds since 1970)
     */
    @Query("SELECT * FROM Contact WHERE Contact.date > (:fromDate) & Contact.date < (:untilDate)")
    fun getContactsWithinTimePeriod(fromDate : Long, untilDate : Long): List<Contact>

    /**
     * Find a contact by searching with a contact ID
     */
    @Query("SELECT * FROM Contact WHERE ID LIKE :ContactID LIMIT 1")
    fun findByName(ContactID: String): Contact

    /**
     * Inserts a contact into the local database on the phone
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Contact: Contact)


    /**
     * Inserts a range of contacts into the local database on the phone
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg Contacts: Contact)

    /**
     * Delete a contacts from the local database on the phone
     * (does not erase the contact IDs from the firestore database)
     */
    @Delete
    fun delete(Contact: Contact)

    /**
     * Delete all contacts from the local database on the phone
     * (does not erase the contact IDs from the firestore database)
     */
    @Query("DELETE FROM Contact")
    fun deleteAll()

    /**
     * Update an existing contact ID.
     */
    @Query("UPDATE Contact SET ID = :newID WHERE ID = :oldID")
    fun update(newID : String, oldID : String)


    /**
     * Update the status of a user's health. Currently it's two options:
     * - Healthy
     * - Sick
     */
    @Query("UPDATE Contact SET health_status = :newHealthStatus WHERE ID = :contactID")
    fun updateHealthStatus(contactID : String, newHealthStatus : String)
}