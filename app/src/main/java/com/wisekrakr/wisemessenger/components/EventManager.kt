package com.wisekrakr.wisemessenger.components

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.api.model.*
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.RequestType
import com.wisekrakr.wisemessenger.api.repository.ChatMessageRepository
import com.wisekrakr.wisemessenger.api.repository.ChatRequestRepository
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoomMessages
import com.wisekrakr.wisemessenger.api.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfileChatRooms
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfiles
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object EventManager {

    fun onGetAllUsers(
        list: ArrayList<UserProfile>,
        setupViewBinding: (ArrayList<UserProfile>) -> Unit,
    ) {
        getUserProfiles()
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    snapshot.children.forEach {
                        val profile = it.getValue(UserProfile::class.java)!!

                        if (profile.uid != FirebaseUtils.firebaseAuth.uid) {
                            list.add(profile)
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


                    if (userProfile!!.chatRooms.isNotEmpty()) {

                        userProfile.chatRooms.keys.forEach {
                            getChatRoom(it).addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val chatRoom = snapshot.getValue(ChatRoom::class.java)

                                        chatRoom?.participants?.forEach { conversationalist ->
                                            if (conversationalist.uid != firebaseAuth.uid) {
                                                getContact(conversationalist.uid)
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                }
                            )
                        }
                    } else {
                        getContact("")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
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
        }.addOnFailureListener {
            Log.d(TAG, "Failed to create new chat room ${it.cause}")
            return@addOnFailureListener
        }

        return chatRoom
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

            avatarRef.putFile(selectedAvatar!!)
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
                    ).addOnCompleteListener {
                        completeListener()
                    }
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