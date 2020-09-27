package com.covid.nodetrace

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Contact (
    @PrimaryKey open val ID : String,
    @PrimaryKey open val date : String,
    @PrimaryKey open val duration : Int,
    @PrimaryKey open val distance : Float,
    @PrimaryKey open val location : Array<Double>) {

}
