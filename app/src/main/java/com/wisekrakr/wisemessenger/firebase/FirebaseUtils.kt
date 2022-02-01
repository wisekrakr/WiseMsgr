package com.wisekrakr.wisemessenger.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wisekrakr.wisemessenger.utils.Config.DATABASE_URL


object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
    val rootReference: DatabaseReference = firebaseDatabase.reference

    fun updateFirebaseUser(displayName: String){
        firebaseAuth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
        )
    }


}