package com.wisekrakr.wisemessenger.components.activity.chat

import android.text.Editable
import android.util.Log
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.repository.ChatMessageRepository
import com.wisekrakr.wisemessenger.appservice.tasks.TaskManager
import com.wisekrakr.wisemessenger.utils.Extensions.TAG

class ChatActivityMethodsImpl {

    fun saveMessage(chatMessage: ChatMessage, chatRoomUid: String, text: Editable){
        ChatMessageRepository.saveChatMessage(
            chatMessage
        ).addOnSuccessListener {
            Log.d(TAG, "Successfully saved Chat Messages to Database")
            addToChatRoom(chatRoomUid, chatMessage,text)
        }
        .addOnFailureListener {
            Log.d(TAG,
                "Failed saving Chat Message to database: ${it.cause}")
        }
    }

    private fun addToChatRoom(chatRoomUid: String, chatMessage: ChatMessage, text: Editable){
        TaskManager.Rooms.onAddChatMessageToChatRoom(chatRoomUid, chatMessage) {
            text.clear()
        }
    }
}