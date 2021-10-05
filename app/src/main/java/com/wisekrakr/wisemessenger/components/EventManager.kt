package com.wisekrakr.wisemessenger.components

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.*
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.model.nondata.RequestType
import com.wisekrakr.wisemessenger.repository.ChatRequestRepository
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.repository.GroupRepository.getGroupsUser
import com.wisekrakr.wisemessenger.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_AVATARS
import com.wisekrakr.wisemessenger.utils.Constants.Companion.STORAGE_BANNERS
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object EventManager {

    fun onGetAllUsers(
        list: ArrayList<UserProfile>,
        setupViewBinding: (ArrayList<UserProfile>) -> Unit,
    ) {

        UserProfileRepository.getUserProfiles()
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
//                            Log.d(FRAGMENT_TAG, it.toString())
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

    fun onGetAllGroupsOfCurrentUser(
        currentUser: User,
        groups: ArrayList<Group>,
        setupViewBinding: (ArrayList<Group>) -> Unit,
    ) {
        getGroupsUser(currentUser.uid).addListenerForSingleValueEvent(object :
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
    fun onCreateNewChatRoom(selectedContacts: ArrayList<Conversationalist>, isPrivate: Boolean): ChatRoom {

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
            RequestType.RECEIVED-> {
                RequestHandler.chatRequestSent(
                    userProfileUid,userProfileUsername, currentUserUid,currentUsername, requestType, completeListener
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

    object RequestHandler{

        fun chatRequestSent(
            userProfileUid: String,
            userProfileUsername:String,
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
            }.addOnFailureListener{ error->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }

            ChatRequestRepository.deleteChatRequest(
                userProfileUid,
                currentUserUid
            ).addOnCompleteListener {
                completeListener()
            }.addOnFailureListener{ error->
                Log.e(TAG, "Failed deleting request ${error.cause}")
            }
        }
    }
}