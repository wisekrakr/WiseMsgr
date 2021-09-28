package com.wisekrakr.wisemessenger.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.utils.Constants

object ChatRoomRepository {

    fun createChatRoom(chatRoom: ChatRoom): Task<Void> {
        val ref = rootReference.child(Constants.REF_CHAT_ROOMS).push()

        chatRoom.uid = ref.key.toString()
        return ref.setValue(chatRoom)
    }

    fun getChatRoom(uid: String): DatabaseReference {
        return rootReference.child(Constants.REF_CHAT_ROOMS).child(uid)
    }

    fun addMessagesToChatRoom(chatRoom: ChatRoom, chatMessage: ChatMessage): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoom.uid)
            .child("messages")
            .child(chatMessage.uid)
            .setValue(chatMessage)
    }
}