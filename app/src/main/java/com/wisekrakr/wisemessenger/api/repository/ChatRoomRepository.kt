package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
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

    fun deleteChatRoom(uid: String): Task<Void> {
        return rootReference.child(Constants.REF_CHAT_ROOMS).child(uid).removeValue()
    }

    fun getChatRoomMessages(uid: String): DatabaseReference {
        return rootReference.child(Constants.REF_CHAT_ROOMS).child(uid).child(Constants.REF_MESSAGES)
    }

    fun addMessageToChatRoom(chatRoom: ChatRoom, chatMessage: ChatMessage): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoom.uid)
            .child(Constants.REF_MESSAGES)
            .child(chatMessage.uid)
            .setValue(chatMessage.chatRoomUid)

    }

    fun removeMessageFromChatRoom(chatRoom: ChatRoom, uid: String): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoom.uid)
            .child(Constants.REF_MESSAGES)
            .child(uid)
            .removeValue()

    }
}