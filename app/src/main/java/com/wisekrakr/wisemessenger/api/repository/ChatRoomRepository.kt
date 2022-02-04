package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.utils.Constants
import java.util.ArrayList

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

    fun addMessageToChatRoom(chatRoomUid: String, chatMessage: ChatMessage): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoomUid)
            .child(Constants.REF_MESSAGES)
            .child(chatMessage.uid)
            .setValue(chatMessage.chatRoomUid)

    }

    fun removeMessageFromChatRoom(chatRoomUid: String, chatMessageUid: String): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoomUid)
            .child(Constants.REF_MESSAGES)
            .child(chatMessageUid)
            .removeValue()

    }

    fun addContactsToChatRoom(chatRoomUid: String, selectedParticipants: ArrayList<Conversationalist>): Task<Void> {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoomUid)
            .child(Constants.REF_CHAT_ROOM_PARTICIPANTS)
            .setValue(selectedParticipants)
    }

    //todo get participants first, loop through till you get the right uid, remove that value
    fun getAllParticipantsOfChatRoom(chatRoom: ChatRoom, conversationalistUid:String): DatabaseReference {

        return rootReference
            .child(Constants.REF_CHAT_ROOMS)
            .child(chatRoom.uid)
            .child(Constants.REF_CHAT_ROOM_PARTICIPANTS)
    }
}