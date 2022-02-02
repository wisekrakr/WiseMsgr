package com.wisekrakr.wisemessenger.components

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wisekrakr.wisemessenger.api.adapter.*
import com.wisekrakr.wisemessenger.api.model.*

object RecyclerViewDataSetup {

    fun contacts(
        contactsAdapter: ContactsAdapter,
        contacts: List<UserProfile>,
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

//        recyclerView.smoothScrollToPosition(chatMessageAdapter.itemCount)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatMessageAdapter
    }

    fun requests(
        chatRequestsAdapter: ChatRequestsAdapter,
        arrayChatRequests: ArrayList<ChatRequest>,
        recyclerView: RecyclerView,
        context: Context
    ){
        chatRequestsAdapter.setData(arrayChatRequests)

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatRequestsAdapter
    }

}