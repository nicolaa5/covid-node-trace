package com.covid.nodetrace

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Contact (
    @PrimaryKey open val ID : String,
    open val date : String,
    open val duration : Int,
    open val distance : Float,
    open val latitude : Double,
    open val longitude : Double) {

}
