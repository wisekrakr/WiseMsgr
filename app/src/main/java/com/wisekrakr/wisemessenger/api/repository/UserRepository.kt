package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.utils.Constants
import java.util.*

object UserRepository {

    fun getCurrentUser(uid: String?): DatabaseReference {
        return rootReference.child(Constants.REF_USERS).child(uid!!)
    }

    fun getUsers(): DatabaseReference {
        return rootReference.child(Constants.REF_USERS)
    }

    fun saveUser(uid: String?): DatabaseReference {
        return rootReference.child(Constants.REF_USERS + "/${uid}")
    }

    fun putDeviceTokenOnUser(uid: String, token: String): Task<Void> {
        return rootReference
            .child(Constants.REF_USERS)
            .child(uid)
            .child("deviceToken")
            .setValue(token)
    }

    fun updateUser(profileUid: String, username: String): Task<Void> {
        val map: HashMap<String, Any> = hashMapOf()
        map["profileUid"] = profileUid
        map["username"] = username
        map["updatedAt"] = Date()

        return rootReference
            .child(Constants.REF_USERS)
            .child(profileUid)
            .updateChildren(map as Map<String, Any>)
    }


}