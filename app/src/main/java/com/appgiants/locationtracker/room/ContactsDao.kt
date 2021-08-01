package com.appgiants.locationtracker.room

import android.content.ClipData
import androidx.room.*
import com.appgiants.locationtracker.Model.ContactList

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contactlist")
    fun getAll(): List<ContactList>

    @Query("SELECT * FROM contactlist WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: Long): ContactList
    @Query("SELECT * FROM contactlist WHERE number IN (:number)")
    fun getContactDetailsFromNumber(number: String): ContactList?


    @Insert()
    fun insertAll(vararg users: ContactList)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: ContactList): Int
    @Delete
    fun delete(user: ContactList)

    @Query("DELETE FROM ContactList")
    fun deleteAll()
}