package com.appgiants.locationtracker.Model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactList(
    @ColumnInfo
    var name: String = "",
    @ColumnInfo
    var number: String = "",
    @PrimaryKey
    var id: Long
)



