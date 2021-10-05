package com.wisekrakr.wisemessenger.repository

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.utils.Constants
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast

object UserProfileRepository {

    fun getCurrentUserProfile(uid: String?): DatabaseReference {
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

    fun updateUserWithANewChatRoom(chatRoom: ChatRoom, userUid:String): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(userUid)
            .child("chatRooms")
            .child(chatRoom.uid)
            .setValue(chatRoom.isPrivate)
    }
}