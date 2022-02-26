package com.wisekrakr.wisemessenger.appservice.tasks

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.model.*
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.api.repository.*
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import java.util.*

object ApiManager {

    object CurrentUser : UserApi {
        override fun onGetUser(userUid: String, continuation: (User) -> Unit) {

            UserRepository.getCurrentUser(userUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUser = snapshot.getValue(User::class.java)

                    if (currentUser != null) {
                        continuation(currentUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
        }

        override fun onUpdateUser(
            profileMap: HashMap<String, String>,
            completeListener: () -> Unit,
            failureListener: () -> Unit,
        ) {
            UserRepository.updateUser(HomeActivity.currentUser!!.uid,
                profileMap["username"].toString())
                .addOnSuccessListener {
                    completeListener()
                }
                .addOnFailureListener {
                    failureListener()
                }
        }

        override fun onSaveUser(continuation: (DataSnapshot) -> Unit) {
            UserRepository.saveUser(firebaseAuth.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
        }

        override fun onPutDeviceTokenOnUser(
            userUid: String,
            deviceToken: String,
            completeListener: () -> Unit,
            failureListener: () -> Unit,
        ) {
            UserRepository.putDeviceTokenOnUser(userUid, deviceToken)
                .addOnCompleteListener {
                    completeListener()
                }.addOnFailureListener {
                    failureListener()
                }
        }
    }

    object Messages : ChatMessageApi {

        override fun onSaveChatMessage(
            chatMessage: ChatMessage,
            chatRoomUid: String,
            continuation: () -> Unit,
        ) {
            ChatMessageRepository.saveChatMessage(
                chatMessage
            ).addOnSuccessListener {

                Rooms.onAddChatMessageToChatRoom(chatRoomUid, chatMessage, continuation)
            }
                .addOnFailureListener {
                    Log.d(TAG,
                        "Failed saving Chat Message to database: ${it.cause}")
                }
        }

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

        override fun onGetChatMessage(
            uid: String,
            messages: ArrayList<ChatMessage>,
            continuation: () -> Unit,
        ) {
            ChatMessageRepository.getChatMessage(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatMessage = snapshot.getValue(ChatMessage::class.java)

                        if (chatMessage != null) {
                            messages.add(chatMessage)

                            continuation()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
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

        override fun onGetAllUserProfiles(
            list: ArrayList<UserProfile>,
            setupViewBinding: (ArrayList<UserProfile>) -> Unit,
        ) {
            UserRepository.getUsers().addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val profile = it.getValue(UserProfile::class.java)!!

                        if (profile.uid.isNotEmpty()) {
                            if (profile.uid != firebaseAuth.uid) {
                                list.add(profile)
                            }
                        }
                    }
                    setupViewBinding(list)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        override fun onGetUser(uid: String, continuation: (UserProfile) -> Unit) {
            UserProfileRepository.getUserProfile(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userProfile = snapshot.getValue(UserProfile::class.java)

                        if (userProfile != null)
                            continuation(userProfile)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Could not get current user profile ${error.message}")
                    }
                })
        }

        override fun onGetUsersByChildValue(
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

        override fun onSaveUserProfile(
            userProfile: UserProfile,
            completeListener: () -> Unit,
            failureListener: () -> Unit,
        ) {

            UserProfileRepository.saveUserProfile(
                userProfile
            )
                .addOnSuccessListener {
                    completeListener()
                }
                .addOnFailureListener {
                    failureListener()
                }
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

        override fun onCreateNewChatRoomForUser(
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

        override fun onAddContactToUserContactList(selectedContacts: ArrayList<Conversationalist>) {
            selectedContacts.forEach { conversationalist ->
                if (conversationalist.uid != firebaseAuth.uid) {

                    /**
                     * Add contact to own contacts list
                     */
                    onUpdateUserWithANewContact(conversationalist,
                        firebaseAuth.currentUser!!.uid)

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

        override fun onUpdateUserWithNewChatRoom(chatRoom: ChatRoom, userUid: String) {
            UserProfileRepository.updateUserWithANewChatRoom(
                chatRoom,
                userUid
            ).addOnSuccessListener {
                Log.d(TAG, "Added chat room to User profile contact list")
            }.addOnFailureListener {
                Log.d(TAG, "Failed to add chat room to user chat rooms ${it.cause}")
                return@addOnFailureListener
            }
        }

        override fun onUpdateUserConnectivityStatus(status: String) {
            UserProfileRepository.updateUserConnectivityStatus(
                firebaseAuth.currentUser?.uid.toString(),
                status
            )
        }

        override fun onSaveUserProfileImage(
            selectedAvatar: Uri?,
            storageRef: String,
            profileMap: HashMap<String, String>,
            updateUserProfile: (HashMap<String, String>) -> Unit,
        ) {
            if (selectedAvatar != null) {
                val fileName = UUID.randomUUID().toString()
                val avatarRef =
                    FirebaseUtils.firebaseStorage.getReference("/$storageRef/$fileName")

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

        override fun onGetUserChatRooms(
            userProfileUid: String,
            findChatRoom: (String) -> Unit,
        ) {
            UserProfileRepository.getUserProfileChatRooms(userProfileUid)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            snapshot.children.forEach {
                                if (it.value == true)
                                    it.key?.let { it1 -> findChatRoom(it1) }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    }
                )
        }

        override fun onDeleteChatRoomFromUser(userUid: String, chatRoomUid: String) {
            UserProfileRepository.deleteChatRoomFromUserProfile(userUid, chatRoomUid)
                .addOnSuccessListener {
                    Log.d(TAG, "Removed chat room from user profile")
                }.addOnFailureListener {
                    Log.d(TAG, "Failure in removing chat room from current user profile")
                }
        }

    }

    object Rooms : ChatRoomApi {

        override fun onGetChatRoom(uid: String, continuation: (chatRoom: ChatRoom) -> Unit) {

            ChatRoomRepository.getChatRoom(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatRoom = snapshot.getValue(ChatRoom::class.java)!!

                        continuation(chatRoom)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

        override fun onGetChatRoom(
            uid: String,
            userProfileUid: String,
            toggleButtons: Unit,
            continuation: (chatRoom: ChatRoom) -> Unit,
        ) {
            ChatRoomRepository.getChatRoom(uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatRoom = snapshot.getValue(ChatRoom::class.java)

                        println("onGetChatRoom $$$$$$$$$$$$$  $chatRoom")

                        chatRoom?.participants?.forEach { conversationalist ->

                            if (conversationalist.uid == userProfileUid) {

                                toggleButtons
                                continuation(chatRoom)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG,
                            "Error in getting chat room for user ${error.message}")
                    }
                }
            )

        }

        override fun onGetChatRoom(
            uid: String,
            conversations: ArrayList<ChatRoom>,
            fragment: Fragment,
            setupViewBinding: (ArrayList<ChatRoom>) -> Unit,
            continuation: (chatRoom: ChatRoom) -> Unit,
        ) {
            ChatRoomRepository.getChatRoom(uid).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatRoom = snapshot.getValue(ChatRoom::class.java)

                        Log.d(TAG, chatRoom.toString())

                        if (chatRoom != null) {
                            if (!conversations.contains(chatRoom)) conversations.add(chatRoom)
                        }
                        if (fragment.isAdded) setupViewBinding(conversations)
                        if (chatRoom != null) continuation(chatRoom)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG,
                            "Error in getting chat room for user ${error.message}")
                    }
                }
            )
        }

        override fun onAddChatMessageToChatRoom(
            uid: String,
            chatMessage: ChatMessage,
            continuation: () -> Unit,
        ) {
            ChatRoomRepository.addMessageToChatRoom(uid, chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully saved Chat Messages to ChatRoom")
                    continuation()
                }.addOnFailureListener {
                    Log.d(TAG,
                        "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                }
        }

        override fun onGetAllChatMessagesOfChatRoom(
            uid: String,
            list: ArrayList<ChatMessage>,
            setupViewBinding: (ArrayList<ChatMessage>) -> Unit,
        ) {
            ChatRoomRepository.getChatRoomMessages(uid)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(
                        snapshot: DataSnapshot,
                        previousChildName: String?,
                    ) {
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
                    override fun onChildMoved(
                        snapshot: DataSnapshot,
                        previousChildName: String?,
                    ) {
                    }

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
                Profiles.onAddContactToUserContactList(selectedContacts)
            }.addOnFailureListener {
                Log.d(TAG, "Failed to create new chat room ${it.cause}")
                return@addOnFailureListener
            }

            return chatRoom
        }

        override fun onUpdateChatRoomWithNewContact(
            uid: String,
            selectedContacts: ArrayList<Conversationalist>,
        ) {
            ChatRoomRepository.addContactsToChatRoom(
                uid, selectedContacts
            ).addOnSuccessListener {
                Log.d(TAG, "Created new chat room")

                Profiles.onAddContactToUserContactList(selectedContacts)


            }.addOnFailureListener {
                Log.d(TAG, "Failed to create new chat room ${it.cause}")
                return@addOnFailureListener
            }
        }

        override fun onRemovingMessageFromChatRoom(uid: String, chatMessageUid: String) {
            ChatRoomRepository.removeMessageFromChatRoom(uid, chatMessageUid)
        }

        override fun onDeleteChatRoom(
            uid: String,
            context: Context,
            toggleButtons: (Boolean) -> Unit,
        ) {
            ChatRoomRepository.deleteChatRoom(uid)
                .addOnCompleteListener {

                    makeToast("You are no longer chatting with this user", context)

                    toggleButtons(true)
                }.addOnFailureListener {
                    makeToast("Failure to delete current chat room", context)
                }
        }

    }

    object Groups : GroupApi {

        override fun onSaveGroup(
            userUid: String,
            group: Group,
            chatRoom: ChatRoom,
            context: Context,
        ) {
            GroupRepository.saveGroup(
                userUid,
                group
            ).addOnSuccessListener {
                makeToast("Successfully created group: ${group.groupName}", context)

                Profiles.onCreateNewChatRoomForUser(chatRoom,
                    userUid)

            }.addOnFailureListener {
                makeToast("Failed to create group: ${group.groupName}", context)
                Log.e(TAG, "Failure in group creation")
            }
        }

        override fun onAddContactToGroup(
            conversationalist: Conversationalist,
            group: Group,
            chatRoom: ChatRoom,
        ) {
            GroupRepository.saveGroup(
                conversationalist.uid,
                group
            ).addOnSuccessListener {
                Profiles.onCreateNewChatRoomForUser(chatRoom, conversationalist.uid)
            }.addOnFailureListener {
                Log.e(TAG, "Failure in group creation")
            }
        }

        override fun onGetAllGroupsOfCurrentUser(
            userUid: String,
            groups: ArrayList<Group>,
            setupViewBinding: (ArrayList<Group>) -> Unit,
        ) {
            GroupRepository.getGroupsUser(userUid).addListenerForSingleValueEvent(object :
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

        override fun onGetAllGroupsOfCurrentUser(
            userUid: String,
            groupUid: String,
            continuation: (Group) -> Unit,
        ) {
            GroupRepository.getGroupsUser(userUid).child(groupUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val group = snapshot.getValue(Group::class.java)

                        if (group != null) {
                            continuation(group)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, error.message)
                    }
                })
        }
    }

    object Notifications : NotificationApi {
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

    object Requests : ChatRequestApi {


        override fun onSaveChatRequest(
            userProfileUid: String,
            userProfileUsername: String,
            currentUserUid: String,
            currentUsername: String,
            requestType: RequestType,
            completeListener: () -> Unit,
            failureListener: () -> Unit,
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
                        completeListener,
                        failureListener
                    )
                }
                RequestType.CANCELLED -> {
                    onDeleteChatRequest(
                        userProfileUid, currentUserUid, completeListener, failureListener
                    )
                }
                RequestType.NONE -> {
                    Log.d(TAG, "NONE as request type")
                }
            }
        }


        override fun onGetAllChatRequestForUser(
            userUid: String,
            requests: ArrayList<ChatRequest>,
            fragment: Fragment,
            setupViewBinding: (ArrayList<ChatRequest>) -> Unit,
        ) {
            ChatRequestRepository.getChatRequestsForCurrentUser(
                userUid
            ).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {
                        val request = it.getValue(ChatRequest::class.java)

                        if (request != null) {
                            Log.d(TAG, request.toString())

                            if (request.requestType == RequestType.RECEIVED)
                                requests.add(request)
                            if (request.requestType == RequestType.SENT)
                                requests.add(request)
                        }
                    }

                    if (fragment.isAdded) {
                        setupViewBinding(requests)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message)
                }
            })
        }

        override fun onGetAllChatRequestForUser(
            userUid: String,
            userProfileUid: String,
            toggleButtons: Unit,
        ) {
            ChatRequestRepository.getChatRequestsForCurrentUser(userUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(userProfileUid)) {
                            toggleButtons
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG,
                            "Error in getting chat requests for user ${error.message}")
                    }
                })
        }

        override fun onDeleteChatRequest(
            uidOne: String,
            uidTwo: String,
            completeListener: () -> Unit,
            failureListener: () -> Unit,
        ) {
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
            failureListener: () -> Unit,
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
                        if (!request.isSuccessful)
                            failureListener
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
            }.addOnFailureListener {
                failureListener
            }
        }

    }

    fun onEndingConversation(
        chatRoom: ChatRoom,
        context: Context,
        toggleButtons: (Boolean) -> Unit,
    ) {
        chatRoom.participants.forEach {
            Profiles.onDeleteChatRoomFromUser(it.uid, chatRoom.uid)
        }

        chatRoom.messages.forEach {
            Messages.onRemovingChatMessage(it.key)
        }

        Rooms.onDeleteChatRoom(chatRoom.uid, context, toggleButtons)
    }

}