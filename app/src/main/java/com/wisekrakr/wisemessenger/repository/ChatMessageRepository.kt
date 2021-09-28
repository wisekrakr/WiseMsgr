package com.wisekrakr.wisemessenger.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.utils.Constants

object ChatMessageRepository {

    fun getAllMessages(): DatabaseReference {
        return FirebaseUtils.rootReference.child(Constants.REF_MESSAGES)
    }

    fun getChatMessage(uid: String): DatabaseReference {
        return FirebaseUtils.rootReference.child(Constants.REF_MESSAGES).child(uid)
    }

    fun saveChatMessage(chatMessage: ChatMessage): Task<Void> {
        val ref = FirebaseUtils.rootReference.child(Constants.REF_MESSAGES).push()

        chatMessage.uid = ref.key.toString()
        return ref.setValue(chatMessage)
    }
}