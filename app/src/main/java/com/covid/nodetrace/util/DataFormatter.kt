package com.covid.nodetrace.util

import com.covid.nodetrace.ui.TimeRange
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DataFormatter {

    fun createDurationFormat(durationInMilliseconds : Long) : String {
        val seconds : Long = TimeUnit.MILLISECONDS.toSeconds(durationInMilliseconds) % 60
        val minutes : Long = TimeUnit.MILLISECONDS.toMinutes(durationInMilliseconds) % 60
        val hours : Long = TimeUnit.MILLISECONDS.toHours(durationInMilliseconds) % 60

        val timeRange : TimeRange = if (minutes < 1L) TimeRange.SEC else if (minutes > 1L && hours < 1L) TimeRange.MIN else TimeRange.HOURS

        when(timeRange) {
            TimeRange.SEC -> {
                return seconds.toString() + " " + timeRange.toString().toLowerCase(Locale.ROOT)
            }
            TimeRange.MIN -> {
                return if (seconds > 9)
                    minutes.toString() + ":" + seconds.toString() + " " +timeRange.toString().toLowerCase(
                        Locale.ROOT)
                else
                    minutes.toString() + ":0" + seconds.toString() + " " + timeRange.toString().toLowerCase(
                        Locale.ROOT)
            }
            TimeRange.HOURS -> {
                return if (seconds > 9 && minutes <= 9)
                    hours.toString() + "0:" + minutes.toString() + ":" + seconds.toString() + " " + timeRange.toString().toLowerCase(
                        Locale.ROOT)
                else if (seconds <= 9 && minutes > 9)
                    hours.toString() + ":" + minutes.toString() + ":0" + seconds.toString() + " " + timeRange.toString().toLowerCase(
                        Locale.ROOT)
                else
                    hours.toString() + ":" + minutes.toString() + ":" + seconds.toString() + " " + timeRange.toString().toLowerCase(
                        Locale.ROOT)
            }
        }
    }

    fun createDistanceFormat(distanceInMeters : Double) : String {
        if (distanceInMeters == -1.0)
            return "0 - 10 m"
        else return distanceInMeters.toString() + " m"
    }

    fun createLocationFormat (latitude : Double, longitude : Double) : String {
        if (latitude == 0.0 || longitude == 0.0)
            return "- , -"
        else return latitude.toBigDecimal().setScale(2, RoundingMode.UP).toString() +", " +
                longitude.toBigDecimal().setScale(2, RoundingMode.UP).toString()
    }

    fun createDateFormat(unixTimeStamp: Long) : String? {
        try {
            val date = SimpleDateFormat("dd-MMM-yy hh:mm")
            val localDate = Date(unixTimeStamp)
            return date.format(localDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}