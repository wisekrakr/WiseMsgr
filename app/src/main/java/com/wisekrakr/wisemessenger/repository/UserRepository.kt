package com.wisekrakr.wisemessenger.repository

import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.utils.Constants

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
}