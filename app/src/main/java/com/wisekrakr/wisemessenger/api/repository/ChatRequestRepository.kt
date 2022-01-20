package com.wisekrakr.wisemessenger.api.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.api.model.ChatRequest
import com.wisekrakr.wisemessenger.utils.Constants

object ChatRequestRepository {

    fun getChatRequestsForCurrentUser(uid: String): DatabaseReference {
        return FirebaseUtils.rootReference.child(Constants.REF_CHAT_REQUESTS).child(uid)
    }

    fun saveChatRequest(chatRequest: ChatRequest): Task<Void> {
        return FirebaseUtils.rootReference.child(Constants.REF_CHAT_REQUESTS)
            .child(chatRequest.from)
            .child(chatRequest.to)
            .setValue(chatRequest)
    }

    fun deleteChatRequest(from: String, to:String): Task<Void> {
        return FirebaseUtils.rootReference.child(Constants.REF_CHAT_REQUESTS)
            .child(from)
            .child(to)
            .removeValue()
    }
}