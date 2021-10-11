package com.wisekrakr.wisemessenger.components

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.adapter.ChatRoomAdapter
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.adapter.GroupsAdapter
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.UserProfile

object RecyclerViewDataSetup {

    fun contacts(
        contactsAdapter: ContactsAdapter,
        contacts: ArrayList<UserProfile>,
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

    fun chatRooms(
        chatRoomAdapter: ChatRoomAdapter,
        arrayChatRooms: ArrayList<ChatRoom>,
        recyclerView: RecyclerView,
        context: Context
    ){
        chatRoomAdapter.setData(arrayChatRooms)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatRoomAdapter
    }

    fun messages(
        chatMessageAdapter: ChatMessageAdapter,
        arrayChatMessages: ArrayList<ChatMessage>,
        recyclerView: RecyclerView,
        context: Context
    ){
        chatMessageAdapter.setData(arrayChatMessages)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatMessageAdapter
    }

}