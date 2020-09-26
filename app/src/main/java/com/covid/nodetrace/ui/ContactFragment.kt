package com.covid.nodetrace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.covid.nodetrace.ContactHistoryAdapter
import com.covid.nodetrace.R

class ContactFragment : Fragment() {

    private lateinit var contactHistoryListView : ListView
    private lateinit var contactHistoryAdapter : ContactHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.contact_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactHistoryListView = view.findViewById(R.id.contact_history_list_view) as ListView

        contactHistoryAdapter = ContactHistoryAdapter(requireActivity())
        contactHistoryListView.adapter = contactHistoryAdapter
        requireActivity().registerForContextMenu(contactHistoryListView)
    }
}