package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.model.*
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.api.repository.*
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import java.util.*

object TaskManager {

    object Messages : ChatMessageApi {
        override fun onGetAllChatMessagesOfChatRoom(
            chatMessageUid: String,
            list: ArrayList<ChatMessage>,
            setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
        ) {
            ChatMessageRepository.getChatMessage(chatMessageUid)
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

        override fun onGetChatMessage(uid: String) {
            TODO("Not yet implemented")
        }

        override fun onRemovingChatMessage(
            chatMessage: ChatMessage,
            chatRoom: ChatRoom,
            context: Context,
        ) {
            ChatMessageRepository.deleteChatMessage(chatMessage.uid)
                .addOnCompleteListener {
                    makeToast("Message Removed", context)

                    Rooms.onRemovingMessageFromChatRoom(chatRoom.uid, chatMessage.uid)
                }.addOnFailureListener {

                    makeToast("Message could not be removed", context)
                }
        }

        override fun onRemovingChatMessage(chatMessageUid: String) {
            ChatMessageRepository.deleteChatMessage(chatMessageUid)
        }

    }

    object Profiles : UserProfileApi {

        override fun onGetUserByChildValue(
            name: String,
            list: ArrayList<UserProfile>,
            setupViewBinding: (ArrayList<UserProfile>) -> Unit,
        ) {
            UserProfileRepository.getUserProfiles()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val profile = it.getValue(UserProfile::class.java)!!

                            if (!profile.uid.isNullOrEmpty()) {
                                if (profile.uid != firebaseAuth.uid) {
                                    if (profile.username.toLowerCase(Locale.ROOT)
                                            .contains(name.toLowerCase(Locale.ROOT))
                                    )
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

        override fun onGetAllContactsOfCurrentUser(getContact: (String) -> Unit) {
            UserProfileRepository.getUserProfile(firebaseAuth.uid)
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

        override fun onCreateNewChatRoomForUserProfile(
            chatRoom: ChatRoom,
            conversationalistUid: String,
        ) {
            UserProfileRepository.updateUserWithANewChatRoom(
                chatRoom,
                conversationalistUid
            ).addOnCompleteListener {
                Log.e(TAG, "Successful chat room creation")

            }.addOnFailureListener {
                Log.e(TAG, "Failure in user profile chat room creation")
            }
        }

        override fun onAddContactToUserProfileContactList(selectedContacts: ArrayList<Conversationalist>) {
            selectedContacts.forEach { conversationalist ->
                if (conversationalist.uid != firebaseAuth.uid) {

                    /**
                     * Add contact to own contacts list
                     */
                    onUpdateUserWithANewContact(conversationalist, firebaseAuth.currentUser!!.uid)

                    /**
                     * Add contact to other contacts list
                     */
                    onUpdateUserWithANewContact(
                        Conversationalist(
                            firebaseAuth.currentUser!!.uid,
                            firebaseAuth.currentUser!!.displayName.toString()
                        ), conversationalist.uid)
                }
            }
        }

        override fun onUpdateUserWithANewContact(
            conversationalist: Conversationalist,
            userUid: String,
        ) {
            UserProfileRepository.updateUserWithANewContact(conversationalist, userUid)
                .addOnSuccessListener {
                    Log.d(TAG, "Added contact to User profile contact list")
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to add contact to user contact list ${it.cause}")
                    return@addOnFailureListener
                }
        }

        override fun onSaveUserProfileImage(
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

        override fun onGetUserProfileChatRooms(
            userProfileUid: String,
            findChatRoom: (String) -> Unit,
        ) {
            UserProfileRepository.getUserProfileChatRooms(userProfileUid).addChildEventListener(
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

        override fun onDeleteChatRoomFromUserProfile(userUid: String, chatRoomUid: String) {
            UserProfileRepository.deleteChatRoomFromUserProfile(userUid, chatRoomUid)
                .addOnSuccessListener {
                    Log.d(TAG, "Removed chat room from user profile")
                }.addOnFailureListener {
                    Log.d(TAG, "Failure in removing chat room from current user profile")
                }
        }

    }

    object Rooms : ChatRoomApi {
        override fun onAddChatMessageToChatRoom(
            chatRoomUid: String,
            chatMessage: ChatMessage,
            extra: () -> Unit,
        ) {
            ChatRoomRepository.addMessageToChatRoom(chatRoomUid, chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully saved Chat Messages to ChatRoom")
                    extra()
                }.addOnFailureListener {
                    Log.d(TAG,
                        "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                }
        }

        override fun onGetAllChatMessagesOfChatRoom(
            chatRoomUid: String,
            list: ArrayList<ChatMessage>,
            setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
        ) {
            ChatRoomRepository.getChatRoomMessages(chatRoomUid)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        Messages.onGetAllChatMessagesOfChatRoom(
                            snapshot.key.toString(),
                            list,
                            setupViewBinding
                        )
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?,
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        override fun onCreateNewChatRoom(
            selectedContacts: ArrayList<Conversationalist>,
            isPrivate: Boolean,
        ): ChatRoom {
            val chatRoom = ChatRoom(selectedContacts, isPrivate)

            ChatRoomRepository.createChatRoom(
                chatRoom
            ).addOnSuccessListener {
                Log.d(TAG, "Created new chat room")
                Profiles.onAddContactToUserProfileContactList(selectedContacts)
            }.addOnFailureListener {
                Log.d(TAG, "Failed to create new chat room ${it.cause}")
                return@addOnFailureListener
            }

            return chatRoom
        }

        override fun onUpdateChatRoomWithNewContact(
            chatRoomUid: String,
            selectedContacts: ArrayList<Conversationalist>,
        ) {
            ChatRoomRepository.addContactsToChatRoom(
                chatRoomUid, selectedContacts
            ).addOnSuccessListener {
                Log.d(TAG, "Created new chat room")

                Profiles.onAddContactToUserProfileContactList(selectedContacts)


            }.addOnFailureListener {
                Log.d(TAG, "Failed to create new chat room ${it.cause}")
                return@addOnFailureListener
            }
        }

        override fun onRemovingMessageFromChatRoom(chatRoomUid: String, chatMessageUid: String) {
            ChatRoomRepository.removeMessageFromChatRoom(chatRoomUid, chatMessageUid)
        }

        override fun onDeleteChatRoom(
            chatRoomUid: String,
            context: Context,
            toggleButtons: (Boolean) -> Unit
        ) {
            ChatRoomRepository.deleteChatRoom(chatRoomUid)
                .addOnCompleteListener {

                    makeToast("You are no longer chatting with this user", context)

                    toggleButtons(true)
                }.addOnFailureListener {
                    makeToast("Failure to delete current chat room",context)
                }
        }

    }

    object Groups : GroupApi {
        override fun onAddContactToGroup(
            conversationalist: Conversationalist,
            group: Group,
            chatRoom: ChatRoom,
        ) {
            GroupRepository.saveGroup(
                conversationalist.uid,
                group
            ).addOnSuccessListener {
                Profiles.onCreateNewChatRoomForUserProfile(chatRoom, conversationalist.uid)
            }.addOnFailureListener {
                Log.e(TAG, "Failure in group creation")
            }
        }

        override fun onGetAllGroupsOfCurrentUser(
            currentUserUid: String,
            groups: ArrayList<Group>,
            setupViewBinding: (ArrayList<Group>) -> Unit,
        ) {
            GroupRepository.getGroupsUser(currentUserUid).addListenerForSingleValueEvent(object :
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

    }

    object Notifications: NotificationApi{
        override fun onPushNotification(
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

    }

    object Requests: ChatRequestApi{

        override fun onSaveChatRequest(
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
                    onChatRequestSent(
                        userProfileUid,
                        userProfileUsername,
                        currentUserUid,
                        currentUsername,
                        requestType,
                        completeListener
                    )
                }
                RequestType.CANCELLED -> {
                    onDeleteChatRequest(
                        userProfileUid, currentUserUid, completeListener
                    )
                }
                RequestType.NONE -> {
                    Log.d(TAG, "NONE as request type")
                }
            }
        }

        override fun onDeleteChatRequest(uidOne: String, uidTwo: String,completeListener: () -> Unit,) {
            ChatRequestRepository.deleteChatRequest(
                uidOne,
                uidTwo
            ).addOnCompleteListener {
                completeListener()
            }.addOnFailureListener { error ->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }

            ChatRequestRepository.deleteChatRequest(
                uidTwo,
                uidOne
            ).addOnCompleteListener {
                completeListener()
            }.addOnFailureListener { error ->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }
        }

        private fun onChatRequestSent(
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

                    Notifications.onPushNotification(
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

    }

    fun onEndingConversation(
        chatRoom: ChatRoom,
        context: Context,
        toggleButtons: (Boolean) -> Unit,
    ) {
        chatRoom.participants.forEach {
            Profiles.onDeleteChatRoomFromUserProfile(it.uid, chatRoom.uid)
        }

        chatRoom.messages.forEach {
            Messages.onRemovingChatMessage(it.key)
        }

        Rooms.onDeleteChatRoom(chatRoom.uid, context, toggleButtons)
    }

}