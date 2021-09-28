package com.wisekrakr.wisemessenger.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wisekrakr.wisemessenger.activity.HomeActivity
import com.wisekrakr.wisemessenger.utils.Config.DATABASE_URL
import com.wisekrakr.wisemessenger.utils.Constants
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import java.net.URI


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

    fun updateProfileUser(activity: Activity, map: HashMap<String,String>){
        rootReference
            .child(Constants.REF_USERS)
            .child(HomeActivity.currentUser!!.uid)
            .updateChildren(map as Map<String, Any>)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    activity.makeToast("Profile successfully updated!")
                }
            }.addOnFailureListener {
                activity.makeToast("Could not update profile right now.")
                Log.e(activity.ACTIVITY_TAG, it.message.toString())
            }
    }
}