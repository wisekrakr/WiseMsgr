package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.util.ArrayList

interface ChatRoomApi {

    fun onAddChatMessageToChatRoom(chatRoomUid: String, chatMessage: ChatMessage, extra:()-> Unit)

    fun onGetAllChatMessagesOfChatRoom(
        chatRoomUid: String,
        list: ArrayList<ChatMessage>,
        setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
    )

    /**
     * Creates a new ChatRoom data object and returns it to be used by a new group
     */
    fun onCreateNewChatRoom(
        selectedContacts: ArrayList<Conversationalist>,
        isPrivate: Boolean,
    ): ChatRoom

    fun onUpdateChatRoomWithNewContact(chatRoomUid: String,selectedContacts: ArrayList<Conversationalist>)

    fun onRemovingMessageFromChatRoom(chatRoomUid:String, chatMessageUid:String)

    fun onDeleteChatRoom(chatRoomUid: String, context: Context, toggleButtons: (Boolean) -> Unit)
}