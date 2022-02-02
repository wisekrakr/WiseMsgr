package com.wisekrakr.wisemessenger.api.repository

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Status
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

object UserProfileRepository {

    fun getUserProfile(childValue: String?): DatabaseReference {
        return rootReference.child(Constants.REF_USER_PROFILES).child(childValue!!)
    }

    fun getUserProfiles(): DatabaseReference {
        return rootReference.child(Constants.REF_USER_PROFILES)
    }

    fun saveUserProfile(userProfile: UserProfile): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(userProfile.uid)
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

    fun updateUserWithANewContact(conversationalist: Conversationalist, userUid:String): Task<Void> {
        return rootReference
            .child(Constants.REF_USER_PROFILES)
            .child(userUid)
            .child(Constants.REF_USER_CONTACTS)
            .child(conversationalist.uid)
            .setValue(conversationalist)
    }

    @SuppressLint("SimpleDateFormat")
    fun updateUserConnectivityStatus(uid: String, state: String) : Task<Void> {

        val calendar: Calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd MMM, yyyy")
        val saveCurrentDate : String = currentDate.format(calendar.time)
        val currentTime = SimpleDateFormat("hh:mm:ss")
        val saveCurrentTime : String = currentTime.format(calendar.time)


        return rootReference.child(Constants.REF_USER_PROFILES)
            .child(uid)
            .child("state")
            .setValue(
                Status(
                    saveCurrentTime,
                    saveCurrentDate,
                    state
                )
            )

    }

}