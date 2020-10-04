package com.covid.nodetrace.database

import androidx.room.*
import com.covid.nodetrace.Contact

@Dao
interface ContactDao {
    @Query("SELECT * FROM Contact")
    fun getAll(): List<Contact>

    @Query("SELECT * FROM Contact WHERE Contact.date > (:fromDate) & Contact.date < (:untilDate)")
    fun getContactsWithinTimePeriod(fromDate : Long, untilDate : Long): List<Contact>

    @Query("SELECT * FROM Contact WHERE ID IN (:ContactListPosition)")
    fun loadAllByPosition(ContactListPosition: IntArray): List<Contact>

    @Query("SELECT * FROM Contact WHERE ID LIKE :ContactID LIMIT 1")
    fun findByName(ContactID: String): Contact

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg Contacts: Contact)

    @Delete
    fun delete(Contact: Contact)

    @Query("DELETE FROM Contact")
    fun deleteAll()

    @Query("UPDATE Contact SET ID = :newID WHERE ID = :oldID")
    fun update(newID : String, oldID : String)
}