package com.wisekrakr.wisemessenger.app

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.adapter.GroupsAdapter
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

object RecyclerViewDataSetup {

    fun contacts(
        contactsAdapter: ContactsAdapter,
        contacts: ArrayList<User>,
        recyclerView: RecyclerView,
        context: Context
    ){
        contactsAdapter.setData(contacts)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = contactsAdapter


        Log.d(TAG, "Showing contacts.... ")
    }

    fun groups(
        groupsAdapter: GroupsAdapter,
        arrayGroups: ArrayList<Group>,
        recyclerView: RecyclerView,
        context: Context
    ){
        groupsAdapter.setData(arrayGroups)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = groupsAdapter
    }
}