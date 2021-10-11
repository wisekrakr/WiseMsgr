package com.wisekrakr.wisemessenger.components.activity.chat

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.adapter.ChatAdapter
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.fragments.PrivateChatFragment.Companion.CHAT_ROOM_KEY
import com.wisekrakr.wisemessenger.components.fragments.PrivateChatFragment.Companion.CONTACT_KEY

import com.wisekrakr.wisemessenger.databinding.ActivityPrivateChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatMessageRepository.getChatMessage
import com.wisekrakr.wisemessenger.repository.ChatMessageRepository.saveChatMessage
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.addMessageToChatRoom
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.getChatRoomMessages
import com.wisekrakr.wisemessenger.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class PrivateChatActivity : BaseActivity<ActivityPrivateChatBinding>() {

    private lateinit var contact: Conversationalist
    private lateinit var userProfile: UserProfile
    private lateinit var chatRoom: ChatRoom
    private lateinit var chatAdapter: ChatAdapter

    private val messagesList: ArrayList<ChatMessage> = ArrayList()

    override val bindingInflater: (LayoutInflater) -> ActivityPrivateChatBinding =
        ActivityPrivateChatBinding::inflate

    override fun setup() {

        contact = intent.getSerializableExtra(CONTACT_KEY) as Conversationalist
        chatRoom = intent.getSerializableExtra(CHAT_ROOM_KEY) as ChatRoom

        Log.d(ACTIVITY_TAG, "Chatting with: ${contact.username} in chat room: ${chatRoom.uid}")

        getCurrentContact()

        chatAdapter = ChatAdapter()

        onShowMessages()

        binding.btnSendMessagePrivateChat.setOnClickListener {
            onSendMessage()
        }
    }

    override fun supportBar() {
        supportActionBar?.title = contact.username
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getCurrentContact() {
        launch {
            getUserProfile(contact.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userProfile = snapshot.getValue(UserProfile::class.java)!!

                        showContactAvatarInActionBar(userProfile)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(ACTIVITY_TAG, "Failure in get user profile ${error.message}")
                    }
                })
        }
    }

    /**
     * Creates a new ChatMessage and saves it in the Firebase Database
     * On Success -> add to ChatRoom messages array for all chat room users
     */
    private fun onSendMessage() {
        launch {

            if (!binding.txtEnterMessagePrivateChat.text.isNullOrEmpty()) {

                val chatMessage = ChatMessage(
                    Conversationalist(
                        firebaseAuth.currentUser?.uid.toString(),
                        firebaseAuth.currentUser?.displayName.toString()),
                    chatRoom.participants,
                    binding.txtEnterMessagePrivateChat.text.toString(),
                    R.color.light_gray,
                    chatRoom.uid
                )

                saveChatMessage(
                    chatMessage
                ).addOnSuccessListener {
                    addMessageToChatRoom(chatRoom, chatMessage)
                        .addOnSuccessListener {
                            Log.d(ACTIVITY_TAG, "Successfully saved Chat Messages to ChatRoom")
                            binding.txtEnterMessagePrivateChat.text.clear()

                        }.addOnFailureListener {
                            Log.d(ACTIVITY_TAG,
                                "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                        }

                }
                    .addOnFailureListener {
                        Log.d(ACTIVITY_TAG,
                            "Failed saving Chat Message to database: ${it.cause}")
                    }
            }
        }
    }

    private fun onShowMessages() {
        launch {

//
//
//            getAllMessages().addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    val chatMessage = snapshot.getValue(ChatMessage::class.java)
//
//                    if (chatMessage != null) { // todo is message part of this chatroom?
//
//                        Log.d(ACTIVITY_TAG, "SHOWING MSG ${chatMessage.message}")
//
//
//
//                        if (chatMessage.sender?.uid == firebaseAuth.currentUser!!.uid) {
//                            chatMessage.messageType = 0
//                        } else {
//                            chatMessage.messageType = 1
//                        }
//                        messagesList.add(chatMessage)
//                        chatAdapter.notifyDataSetChanged()
//                    }
//
//                    Log.d(ACTIVITY_TAG, "Adding message to list ${chatMessage?.message}")
//
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
//                override fun onChildRemoved(snapshot: DataSnapshot) {}
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//                override fun onCancelled(error: DatabaseError) {}
//            })

            getChatRoomMessages(chatRoom.uid).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    getChatMessage(snapshot.key.toString()).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                                if (chatMessage != null) {
                                    messagesList.add(chatMessage)
                                    chatAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        }
                    )
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })

            chatAdapter.setData(messagesList)

            binding.recyclerViewPrivateChat.layoutManager = LinearLayoutManager(
                this@PrivateChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.recyclerViewPrivateChat.setHasFixedSize(true)
            binding.recyclerViewPrivateChat.adapter = chatAdapter


        }
    }


    @SuppressWarnings
    private fun showContactAvatarInActionBar(contact: UserProfile) {
        supportActionBar?.displayOptions =
            supportActionBar!!.displayOptions or ActionBar.DISPLAY_SHOW_CUSTOM

        val circleImageView = CircleImageView(supportActionBar!!.themedContext)
        circleImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        Actions.ImageActions.loadImage(
            contact.avatarUrl,
            circleImageView
        )

        val layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT, Gravity.END
        )
//        layoutParams.rightMargin = 10
        circleImageView.layoutParams = layoutParams
        supportActionBar?.customView = circleImageView
    }

}