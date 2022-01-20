package com.wisekrakr.wisemessenger

import android.util.Log
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.utils.Extensions.TAG
import org.junit.Test

class ChatRoomRepositoryTests {

    @Test
    fun testCreateChatRoom(){

        ChatRoomRepository.createChatRoom(
            ChatRoom(
                arrayListOf(
                    FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
                    "123"
                ),
                arrayListOf()
            )
        ).addOnSuccessListener {
            ChatRoomRepository.getCreatedChatRoomData(object : ChatRoomRepository.FirebaseCallback {
                override fun onCallback(chatRoom: ChatRoom) {
                    Log.d(TAG, "New chat room created ${chatRoom.uid}")

                }
            })
        }.addOnFailureListener {
            Log.d(TAG, "Failed to create new chat room ${it.cause}")
        }
    }
}