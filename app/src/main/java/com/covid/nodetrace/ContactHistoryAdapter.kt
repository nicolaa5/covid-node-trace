package com.covid.nodetrace

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ContactHistoryAdapter(context: Context) : BaseAdapter() {

    private val mInflator: LayoutInflater

    private var mContacts : List<Contact> = listOf()

    init {
        mInflator = LayoutInflater.from(context)
    }

    fun updateValues (contacts : List<Contact>) {
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

        val contact : Contact = mContacts.get(position)
        row.contactDate.text = contact.date
        row.contactDuration.text = contact.duration.toString()
        row.contactDistance.text = contact.distance.toString()
        row.contactLocation.text = contact.latitude.toString() + ", " + contact.longitude.toString()
        return view
    }
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