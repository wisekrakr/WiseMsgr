package com.wisekrakr.wisemessenger.appservice.tasks

import android.net.Uri
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import java.util.ArrayList
import java.util.HashMap

interface UserProfileApi {

    fun onGetUser(uid: String, continuation:(UserProfile)->Unit)

    fun onGetUsersByChildValue(
        name: String,
        list: ArrayList<UserProfile>,
        setupViewBinding: (ArrayList<UserProfile>) -> Unit,
    )

    fun onSaveUserProfile(
        userProfile: UserProfile, completeListener: () -> Unit, failureListener: () -> Unit,
    )

    fun onGetAllContactsOfCurrentUser(getContact: (String) -> Unit)

    /**
     * Every User Profile has a MutableList of chat room uids
     * In this function we update the User Profile by adding the newly made chat room
     * to that mutable list of chat rooms.
     * They consist of private and non-private (group) chat rooms
     * @param chatRoom the newly made chat room with 2 participants
     * @param conversationalistUid user uid
     */
    fun onCreateNewChatRoomForUser(chatRoom: ChatRoom, conversationalistUid: String)

    fun onAddContactToUserContactList(selectedContacts: ArrayList<Conversationalist>)

    fun onUpdateUserWithANewContact(conversationalist: Conversationalist, userUid: String)

    fun onUpdateUserWithNewChatRoom(chatRoom: ChatRoom, userUid: String)

    /**
     * Set User profile to 'Online' or 'Offline'
     * @param status String 'Online' or 'Offline'
     */
    fun onUpdateUserConnectivityStatus(status: String)

    fun onSaveUserProfileImage(
        selectedAvatar: Uri?,
        storageRef: String,
        profileMap: HashMap<String, String>,
        updateUserProfile: (HashMap<String, String>) -> Unit,
    )

    fun onGetUserChatRooms(userProfileUid: String, findChatRoom: (String) -> Unit)

    fun onDeleteChatRoomFromUser(userUid: String, chatRoomUid: String)

    fun onGetAllUserProfiles(list: ArrayList<UserProfile>,
                             setupViewBinding: (ArrayList<UserProfile>) -> Unit,)
}