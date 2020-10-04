package com.covid.nodetrace

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.covid.nodetrace.ui.HealthStatusFragment

public enum class HealthStatus {
    HEALTHY,
    SICK
}

@Entity
open class Contact (
    @PrimaryKey open val ID : String,
    @ColumnInfo(name = "date") open val date : Long,
    @ColumnInfo(name = "latitude") open var latitude : Double = 0.0,
    @ColumnInfo(name = "longitude") open var longitude : Double = 0.0,
    @ColumnInfo(name = "duration") open var duration : Long = -1,
    @ColumnInfo(name = "distance") open var distance : Double = -1.0,
    @ColumnInfo(name = "health_status") open var healthStatus : HealthStatus = HealthStatus.HEALTHY) {
}
