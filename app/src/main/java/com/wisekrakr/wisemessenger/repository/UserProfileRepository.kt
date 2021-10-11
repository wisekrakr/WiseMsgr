package com.wisekrakr.wisemessenger.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Constants

object UserProfileRepository {

    fun getUserProfile(uid: String?): DatabaseReference {
        return rootReference.child(Constants.REF_USER_PROFILES).child(uid!!)
    }

    fun getUserProfiles(): DatabaseReference {
        return rootReference.child(Constants.REF_USER_PROFILES)
    }

    fun saveUserProfile(userProfile: UserProfile): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(HomeActivity.currentUser!!.uid)
            .setValue(userProfile)
    }

    fun getUserProfileChatRooms(currentUserUid:String): DatabaseReference {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(currentUserUid)
            .child(Constants.REF_USER_CHAT_ROOMS)
    }

    fun deleteChatRoomFromUserProfile(currentUserUid:String, chatRoomUid: String): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(currentUserUid)
            .child(Constants.REF_USER_CHAT_ROOMS)
            .child(chatRoomUid)
            .removeValue()
    }

    fun updateUserWithANewChatRoom(chatRoom: ChatRoom, userUid:String): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(userUid)
            .child(Constants.REF_USER_CHAT_ROOMS)
            .child(chatRoom.uid)
            .setValue(chatRoom.isPrivate)
    }

}