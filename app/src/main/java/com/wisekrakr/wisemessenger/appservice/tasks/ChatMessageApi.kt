package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import java.util.*

interface ChatMessageApi {
    fun onSaveChatMessage(chatMessage: ChatMessage, chatRoomUid: String, continuation: () -> Unit)

    fun onGetAllChatMessagesOfChatRoom(
        chatRoomUid: String,
        list: ArrayList<ChatMessage>,
        setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
    )

    fun onGetChatMessage(uid: String, messages: ArrayList<ChatMessage>, continuation: () -> Unit)

    fun onRemovingChatMessage(
        chatMessage: ChatMessage,
        chatRoom: ChatRoom,
        context: Context,
    )

    fun onRemovingChatMessage(
        chatMessageUid: String,
    )
}