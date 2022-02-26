package com.wisekrakr.wisemessenger.appservice.tasks

import com.google.firebase.database.DataSnapshot
import com.wisekrakr.wisemessenger.api.model.User
import java.util.HashMap

interface UserApi {
    fun onGetUser(userUid: String, continuation: (User) -> Unit)
    fun onUpdateUser(
        profileMap: HashMap<String, String>, completeListener: () -> Unit,
        failureListener: () -> Unit,
    )

    fun onSaveUser(continuation: (DataSnapshot) -> Unit)

    fun onPutDeviceTokenOnUser(
        userUid: String, deviceToken: String, completeListener: () -> Unit,
        failureListener: () -> Unit,
    )
}