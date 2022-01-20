package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.rootReference
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.utils.Constants

object GroupRepository {

    fun saveGroup(uid: String, group: Group): Task<Void> {
        val ref = rootReference.child(Constants.REF_GROUPS + "/$uid").push()
        group.uid = ref.key.toString()
        return ref.setValue(group)
    }

    fun getGroupsUser(userUid: String): DatabaseReference {
        return rootReference.child(Constants.REF_GROUPS).child(userUid)
    }

    fun getGroup(userUid: String, groupUid:String): DatabaseReference {
        return rootReference.child(Constants.REF_GROUPS).child(userUid).child(groupUid)
    }
}