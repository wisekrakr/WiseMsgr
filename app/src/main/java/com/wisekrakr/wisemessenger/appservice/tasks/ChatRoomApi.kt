package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import androidx.fragment.app.Fragment
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.util.*

interface ChatRoomApi {

    /**
     * Get chat room by uid
     * @param uid String chat room uid
     * @param continuation Unit handling of the chat room found within this method
     */
    fun onGetChatRoom(
        uid: String,
        continuation: (chatRoom: ChatRoom) -> Unit,
    )

    /**
     * Get chat room by uid and toggle buttons
     * @param uid String chat room uid
     * @param userProfileUid String user profile uid
     * @param toggleButtons Unit handling toggling of buttons
     * @param continuation Unit handling of the chat room found within this method
     */
    fun onGetChatRoom(
        uid: String,
        userProfileUid: String,
        toggleButtons: Unit,
        continuation: (chatRoom: ChatRoom) -> Unit,
    )

    /**
     * Get chat room by uid and setup viewbinding
     * @param uid String chat room uid
     * @param conversations ArrayList of chat rooms
     * @param fragment Fragment current fragment working in
     * @param setupViewBinding Unit for handling filling the recycler view with chat room conversations
     * @param continuation Unit handling of the chat room found within this method
     */
    fun onGetChatRoom(
        uid: String,
        conversations: ArrayList<ChatRoom>,
        fragment: Fragment,
        setupViewBinding: (ArrayList<ChatRoom>) -> Unit,
        continuation: (chatRoom: ChatRoom) -> Unit,
    )

    /**
     * Adds a chat message to a Firebase Database chat room
     * @param uid String chat room uid
     * @param chatMessage ChatMessage
     * @param continuation Unit handling of any extra elements within the Activity or Fragment this method is used in
     */
    fun onAddChatMessageToChatRoom(uid: String, chatMessage: ChatMessage, continuation: () -> Unit)

    /**
     * Gets all chat messages within a chat room in the Firebase Database
     * @param uid String chat room uid
     * @param list ArrayList of chat messages
     * @param setupViewBinding Unit for handling filling the recycler view with chat room messages
     */
    fun onGetAllChatMessagesOfChatRoom(
        uid: String,
        list: ArrayList<ChatMessage>,
        setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
    )

    /**
     * Creates a new ChatRoom data object and returns it to be used by a new group
     * @param selectedContacts ArrayList of Conversationalist
     * @param isPrivate Boolean Group conversations are not private, but Private conversation are
     * @return the Chat Room that was newly created
     */
    fun onCreateNewChatRoom(
        selectedContacts: ArrayList<Conversationalist>,
        isPrivate: Boolean,
    ): ChatRoom

    /**
     * Add a Conversationalist to the participants within a chat room
     * @param uid String chat room uid
     * @param selectedContacts ArrayList of Conversationalist
     */
    fun onUpdateChatRoomWithNewContact(
        uid: String,
        selectedContacts: ArrayList<Conversationalist>,
    )

    /**
     * Remove a Chat Message from a Chat Room in the Firebase Database
     * @param uid String chat room uid
     * @param chatMessageUid String uid of chat message to remove
     */
    fun onRemovingMessageFromChatRoom(uid: String, chatMessageUid: String)

    /**
     * Delete a chat room from the Firebase Database
     * @param uid String chat room uid
     * @param context Context of Activity or Fragment
     * @param toggleButtons Unit handling toggling of buttons
     */
    fun onDeleteChatRoom(uid: String, context: Context, toggleButtons: (Boolean) -> Unit)
}