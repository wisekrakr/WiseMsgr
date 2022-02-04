package com.wisekrakr.wisemessenger.components

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.model.*
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.api.repository.*
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoomMessages
import com.wisekrakr.wisemessenger.api.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfileChatRooms
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfiles
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import java.util.*

object EventManager {

    fun onGetUserByChildValue(
        name: String,
        list: ArrayList<UserProfile>,
        setupViewBinding: (ArrayList<UserProfile>) -> Unit,
    ) {
        getUserProfiles()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val profile = it.getValue(UserProfile::class.java)!!

                        if (!profile.uid.isNullOrEmpty()) {
                            if (profile.uid != firebaseAuth.uid) {
                                if (profile.username.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))
                                list.add(profile)
                            }
                        }
                    }

                    setupViewBinding(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
    }


    fun onGetAllContactsOfCurrentUser(
        getContact: (String) -> Unit,
    ) {
        getUserProfile(firebaseAuth.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserProfile::class.java)

                    if (!userProfile?.uid.isNullOrEmpty()) {
                        if (userProfile!!.contacts.isNotEmpty()) {
                            userProfile.contacts.values.forEach { conversationalist ->
                                if (conversationalist.uid != firebaseAuth.uid) {
                                    getContact(conversationalist.uid)
                                }
                            }
                        } else {
                            getContact("")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun onAddChatMessageToChatRoom(chatRoomUid: String, chatMessage: ChatMessage, extra:()-> Unit) {
        ChatRoomRepository.addMessageToChatRoom(chatRoomUid, chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved Chat Messages to ChatRoom")
                extra()
            }.addOnFailureListener {
                Log.d(TAG,
                    "Failed saving Chat Messages to ChatRoom: ${it.cause}")
            }
    }

    fun onGetAllChatMessagesOfChatRoom(
        chatRoomUid: String,
        list: ArrayList<ChatMessage>,
        setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
    ) {
        getChatRoomMessages(chatRoomUid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                ChatMessageRepository.getChatMessage(snapshot.key.toString())
                    .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                                if (chatMessage != null) {

                                    if (chatMessage.sender?.uid == firebaseAuth.currentUser!!.uid) {
                                        chatMessage.messageType = 0
                                    } else {
                                        chatMessage.messageType = 1
                                    }

                                    list.add(chatMessage)
                                }

                                setupViewBinding(list)

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        }
                    )

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun onAddContactToGroup(conversationalist: Conversationalist, group: Group, chatRoom: ChatRoom){
        GroupRepository.saveGroup(
            conversationalist.uid,
            group
        ).addOnSuccessListener {
            Log.e(TAG, "Failure in group creation")

            onCreateNewChatRoomForUserProfile(chatRoom, conversationalist.uid)

        }.addOnFailureListener {
            Log.e(TAG, "Failure in group creation")
        }
    }



    fun onGetAllGroupsOfCurrentUser(
        currentUserUid: String,
        groups: ArrayList<Group>,
        setupViewBinding: (ArrayList<Group>) -> Unit,
    ) {
        getGroupsUser(currentUserUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val group = it.getValue(Group::class.java)!!

                    if (group.groupName.isNotEmpty()) {
                        groups.add(group)
                    }
                }

                setupViewBinding(groups)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        })
    }

    /**
     * Creates a new ChatRoom data object and returns it to be used by a new group
     */
    fun onCreateNewChatRoom(
        selectedContacts: ArrayList<Conversationalist>,
        isPrivate: Boolean,
    ): ChatRoom {

        val chatRoom = ChatRoom(selectedContacts, isPrivate)

        ChatRoomRepository.createChatRoom(
            chatRoom
        ).addOnSuccessListener {
            Log.d(TAG, "Created new chat room")

            onAddContactToUserProfileContactList(selectedContacts)

        }.addOnFailureListener {
            Log.d(TAG, "Failed to create new chat room ${it.cause}")
            return@addOnFailureListener
        }

        return chatRoom
    }

    fun onUpdateChatRoomWithNewContact(chatRoomUid: String,selectedContacts: ArrayList<Conversationalist>) {
        ChatRoomRepository.addContactsToChatRoom(
            chatRoomUid, selectedContacts
        ).addOnSuccessListener {
            Log.d(TAG, "Created new chat room")

            onAddContactToUserProfileContactList(selectedContacts)

        }.addOnFailureListener {
            Log.d(TAG, "Failed to create new chat room ${it.cause}")
            return@addOnFailureListener
        }
    }

    /**
     * Every User Profile has a MutableList of chat room uids
     * In this function we update the User Profile by adding the newly made chat room
     * to that mutable list of chat rooms.
     * They consist of private and non-private (group) chat rooms
     * @param chatRoom the newly made chat room with 2 participants
     * @param conversationalistUid user uid
     */
    fun onCreateNewChatRoomForUserProfile(chatRoom: ChatRoom, conversationalistUid: String){

        UserProfileRepository.updateUserWithANewChatRoom(
            chatRoom,
            conversationalistUid
        ).addOnCompleteListener {
            Log.e(TAG, "Successful chat room creation")

        }.addOnFailureListener {
            Log.e(TAG, "Failure in user profile chat room creation")
        }

    }

    fun onAddContactToUserProfileContactList(
        selectedContacts: ArrayList<Conversationalist>,
    ) {
        selectedContacts.forEach { conversationalist ->
            if (conversationalist.uid != firebaseAuth.uid) {
                /**
                 * Add contact to own contacts list
                 */
                UserProfileRepository.updateUserWithANewContact(
                    conversationalist,
                    firebaseAuth.currentUser!!.uid)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added contact to User profile contact list")
                    }.addOnFailureListener {
                        Log.d(TAG, "Failed to add contact to user contact list ${it.cause}")
                        return@addOnFailureListener
                    }

                /**
                 * Add contact to other contacts list
                 */
                UserProfileRepository.updateUserWithANewContact(
                    Conversationalist(
                        firebaseAuth.currentUser!!.uid,
                        firebaseAuth.currentUser!!.displayName.toString()
                    ), conversationalist.uid)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added contact to User profile contact list")
                    }.addOnFailureListener {
                        Log.d(TAG, "Failed to add contact to user contact list ${it.cause}")
                        return@addOnFailureListener
                    }
            }
        }
    }

    fun onSaveUserProfileImage(
        selectedAvatar: Uri?,
        storageRef: String,
        profileMap: HashMap<String, String>,
        updateUserProfile: (HashMap<String, String>) -> Unit,
    ) {

        if (selectedAvatar != null) {
            val fileName = UUID.randomUUID().toString()
            val avatarRef = FirebaseUtils.firebaseStorage.getReference("/$storageRef/$fileName")

            avatarRef.putFile(selectedAvatar)
                .addOnSuccessListener { it ->

                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    avatarRef.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")

                        when (storageRef) {
                            STORAGE_AVATARS -> {
                                profileMap["avatarUrl"] = it.toString()
                            }
                            STORAGE_BANNERS -> {
                                profileMap["bannerUrl"] = it.toString()
                            }
                        }
                        updateUserProfile(profileMap)
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed uploading image: ${it.cause}")
                    return@addOnFailureListener
                }
        }
    }

    fun onGetChatRooms(userProfileUid: String, findChatRoom: (String) -> Unit) {
        getUserProfileChatRooms(userProfileUid).addChildEventListener(
            object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    if (snapshot.value == true)
                        findChatRoom(snapshot.key.toString())
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?,
                ) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    fun onPushNotification(
        userProfileUid: String,
        userProfileUsername: String,
        currentUserUid: String,
        currentUsername: String,
        message: String,
        notificationType: NotificationType,
    ) {
        NotificationRepository.saveNotification(
            Notification(
                userProfileUid,
                userProfileUsername,
                currentUserUid,
                currentUsername,
                message,
                notificationType
            )
        ).addOnCompleteListener { notification ->
            if (notification.isSuccessful)
                Log.d(TAG, "Successfully pushed notification")
        }
    }

    fun onRemovingChatMessage(
        chatMessage: ChatMessage,
        chatRoom: ChatRoom,
        context: Context,
    ) {
        ChatMessageRepository.deleteChatMessage(chatMessage.uid)
            .addOnCompleteListener {
                Toast.makeText(context,
                    "Message Removed",
                    Toast.LENGTH_SHORT).show()


                ChatRoomRepository.removeMessageFromChatRoom(chatRoom.uid, chatMessage.uid)
            }.addOnFailureListener {
                Toast.makeText(context, "Message could not be removed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun onEndingConversation(
        chatRoom: ChatRoom,
        context: Context,
        toggleButtons: (Boolean) -> Unit,
    ) {
        chatRoom.participants.forEach {
            UserProfileRepository.deleteChatRoomFromUserProfile(it.uid, chatRoom.uid)
                .addOnSuccessListener {
                    Log.d(TAG, "Removed chat room from user profile")
                }.addOnFailureListener {
                    Log.d(TAG, "Failure in removing chat room from current user profile")
                }
        }

        chatRoom.messages.forEach {
            ChatMessageRepository.deleteChatMessage(it.key)
        }

        ChatRoomRepository.deleteChatRoom(chatRoom.uid)
            .addOnCompleteListener {
                Toast.makeText(context,
                    "You are no longer chatting with this user",
                    Toast.LENGTH_SHORT).show()

                toggleButtons(true)
            }.addOnFailureListener {
                Toast.makeText(context, "Failure to delete current chat room", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun onSaveChatRequest(
        userProfileUid: String,
        userProfileUsername: String,
        currentUserUid: String,
        currentUsername: String,
        requestType: RequestType,
        completeListener: () -> Unit,
    ) {
        when (requestType) {
            RequestType.SENT,
            RequestType.ACCEPT,
            RequestType.RECEIVED,
            -> {
                RequestHandler.chatRequestSent(
                    userProfileUid,
                    userProfileUsername,
                    currentUserUid,
                    currentUsername,
                    requestType,
                    completeListener
                )
            }
            RequestType.CANCELLED -> {
                RequestHandler.chatRequestCancelled(
                    userProfileUid, currentUserUid, completeListener
                )
            }
            RequestType.NONE -> {
                Log.d(TAG, "NONE as request type")
            }
        }


    }



    object RequestHandler {

        fun chatRequestSent(
            userProfileUid: String,
            userProfileUsername: String,
            currentUserUid: String,
            currentUsername: String,
            requestType: RequestType,
            completeListener: () -> Unit,
        ) {
            ChatRequestRepository.saveChatRequest(
                ChatRequest(
                    userProfileUid,
                    userProfileUsername,
                    currentUserUid,
                    currentUsername,
                    requestType
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    ChatRequestRepository.saveChatRequest(
                        ChatRequest(
                            currentUserUid,
                            currentUsername,
                            userProfileUid,
                            userProfileUsername,
                            RequestType.RECEIVED
                        )
                    ).addOnCompleteListener { request ->
                        if (request.isSuccessful)
                            completeListener()
                    }

                    onPushNotification(
                        userProfileUid,
                        userProfileUsername,
                        currentUserUid,
                        currentUsername,
                        "Chat Request from $currentUsername",
                        NotificationType.CHAT_REQUEST
                    )
                }
            }
        }

        /**
         * Remove request from Current User's and "other" User's Chat Request list
         */
        fun chatRequestCancelled(
            userProfileUid: String,
            currentUserUid: String,
            completeListener: () -> Unit,
        ) {
            ChatRequestRepository.deleteChatRequest(
                currentUserUid,
                userProfileUid
            ).addOnCompleteListener {
                completeListener()
            }.addOnFailureListener { error ->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }

            ChatRequestRepository.deleteChatRequest(
                userProfileUid,
                currentUserUid
            ).addOnCompleteListener {
                completeListener()
            }.addOnFailureListener { error ->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }
        }
    }
}