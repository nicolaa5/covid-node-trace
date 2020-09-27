package com.covid.nodetrace

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
class Contact {
    @PrimaryKey val ID : String
    @ColumnInfo(name = "date") val date : Long
    @ColumnInfo(name = "latitude") var latitude : Double = -1.0
    @ColumnInfo(name = "longitude") var longitude : Double = -1.0
    @ColumnInfo(name = "duration") var duration : Long = -1
    @ColumnInfo(name = "distance") var distance : Double = -1.0

    constructor(contactID : String, contactDate : Long) {
        ID = contactID
        date = contactDate
    }

    constructor(contactID : String, contactDate : Long, lat : Double ,long : Double) {
        ID = contactID
        date = contactDate
        latitude = lat
        longitude = long
    }

    constructor(contactID : String, contactDate : Long, lat : Double ,long : Double , contactDuration : Long, contactDistance : Double ) {
        ID = contactID
        date = contactDate
        latitude = lat
        longitude = long
        duration = contactDuration
        distance = contactDistance
    }

}
