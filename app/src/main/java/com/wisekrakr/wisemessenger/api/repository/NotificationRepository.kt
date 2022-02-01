package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.api.model.Notification
import com.wisekrakr.wisemessenger.utils.Constants

object NotificationRepository {

    fun getNotificationsForCurrentUser(uid: String): DatabaseReference {
        return FirebaseUtils.rootReference.child(Constants.REF_NOTIFICATIONS).child(uid)
    }

    fun saveNotification(notification: Notification): Task<Void> {
        return FirebaseUtils.rootReference.child(Constants.REF_NOTIFICATIONS)
            .child(notification.to)
            .child(notification.from)
            .setValue(notification)
    }

    fun deleteNotification(from: String, to:String): Task<Void> {
        return FirebaseUtils.rootReference.child(Constants.REF_NOTIFICATIONS)
            .child(from)
            .child(to)
            .removeValue()
    }
}