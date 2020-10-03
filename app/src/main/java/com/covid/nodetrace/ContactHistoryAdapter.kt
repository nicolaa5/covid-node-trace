package com.covid.nodetrace

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ContactHistoryAdapter(context: Context) : BaseAdapter() {

    private val mInflator: LayoutInflater
    private val mContext = context

    private var mContacts : List<Contact> = listOf()

    init {
        mInflator = LayoutInflater.from(context)
    }

    fun updateValues(contacts: List<Contact>) {
        mContacts = contacts
        rerenderList()
    }

    override fun getItem(position: Int): Contact {
        return mContacts.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

    override fun getCount(): Int {
        return mContacts.size
    }

    fun rerenderList () {
        notifyDataSetChanged()
    }

    fun createDurationString(durationInMilliseconds : Long) : String {
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
                        minutes.toString() + ":" + seconds.toString() + " " +timeRange.toString().toLowerCase(Locale.ROOT)
                    else
                        minutes.toString() + ":0" + seconds.toString() + " " + timeRange.toString().toLowerCase(Locale.ROOT)
            }
            TimeRange.HOURS -> {
                return if (seconds > 9 && minutes <= 9)
                        hours.toString() + "0:" + minutes.toString() + ":" + seconds.toString() + " " + timeRange.toString().toLowerCase(Locale.ROOT)
                    else if (seconds <= 9 && minutes > 9)
                        hours.toString() + ":" + minutes.toString() + ":0" + seconds.toString() + " " + timeRange.toString().toLowerCase(Locale.ROOT)
                    else
                        hours.toString() + ":" + minutes.toString() + ":" + seconds.toString() + " " + timeRange.toString().toLowerCase(Locale.ROOT)
            }
        }
    }

    fun createDistanceFormat(distanceInMeters : Double) : String {
        if (distanceInMeters == -1.0)
            return "0 - 10 m"
        else return distanceInMeters.toString() + " m"
    }

    fun createLocationFormat (latitude : Double, longitude : Double) : String {
        if (latitude == -1.0 || longitude == -1.0)
            return ""
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


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val row: ContactRow
        if (convertView == null) {
            view = mInflator.inflate(R.layout.contact_history_row, parent, false)
            row = ContactRow(view)
            view.tag = row
        } else {
            view = convertView
            row = view.tag as ContactRow
        }

        if (position == 0) {
            row.contactDate.text = mContext.resources.getString(R.string.row_contact_date)
            row.contactDuration.text = mContext.resources.getString(R.string.row_contact_duration)
            row.contactDistance.text = mContext.resources.getString(R.string.row_contact_distance)
            row.contactLocation.text = mContext.resources.getString(R.string.row_contact_location)

        }
        else {
            val contact : Contact = mContacts.get(position)
            row.contactDate.text = createDateFormat(contact.date)
            row.contactDuration.text = createDurationString(contact.duration)
            row.contactDistance.text = createDistanceFormat(contact.distance)
            row.contactLocation.text = createLocationFormat(contact.latitude, contact.longitude)
        }
        return view
    }
}

public enum class TimeRange {
    SEC,
    MIN,
    HOURS
}




private class ContactRow(row: View?) {
    public val contactDate: TextView
    public val contactDuration: TextView
    public val contactDistance: TextView
    public val contactLocation: TextView

    init {
        contactDate = row?.findViewById(R.id.row_contact_date) as TextView
        contactDuration = row?.findViewById(R.id.row_contact_duration) as TextView
        contactDistance = row?.findViewById(R.id.row_contact_distance) as TextView
        contactLocation = row?.findViewById(R.id.row_contact_location) as TextView
    }
}