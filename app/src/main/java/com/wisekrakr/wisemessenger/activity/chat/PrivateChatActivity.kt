package com.wisekrakr.wisemessenger.activity.chat

import android.annotation.SuppressLint
import android.app.ActionBar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.activity.BaseActivity

import com.wisekrakr.wisemessenger.adapter.ChatAdapter
import com.wisekrakr.wisemessenger.databinding.ActivityPrivateChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.fragments.ContactsFragment.Companion.CHAT_ROOM_KEY
import com.wisekrakr.wisemessenger.fragments.ContactsFragment.Companion.CONTACT_KEY
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.addMessagesToChatRoom
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Constants
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class PrivateChatActivity : BaseActivity<ActivityPrivateChatBinding>() {

    private lateinit var contact: User
    private lateinit var chatRoomUid: String
    private lateinit var chatAdapter: ChatAdapter

    private val messagesList: ArrayList<ChatMessage> = ArrayList()

    override val bindingInflater: (LayoutInflater) -> ActivityPrivateChatBinding =
        ActivityPrivateChatBinding::inflate

    override fun setup() {

        contact = intent.getSerializableExtra(CONTACT_KEY) as User
        chatRoomUid = intent.getSerializableExtra(CHAT_ROOM_KEY) as String

        Log.d(ACTIVITY_TAG, "Chatting with: ${contact.username} in chat room: $chatRoomUid")

        showContactAvatarInActionBar(contact)

        chatAdapter = ChatAdapter()

        onShowMessages()

        binding.btnSendMessageChat.setOnClickListener {
            onSendMessage()
        }
    }

    override fun supportBar() {
        supportActionBar?.title = contact.username
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Creates a new ChatMessage and saves it in the Firebase Database
     * On Success -> add to ChatRoom messages array for all chat room users
     */
    private fun onSendMessage() {
//        launch {
//            addMessagesToChatRoom(chatRoom, ChatMessage(
//                Conversationalist(
//                    firebaseAuth.currentUser?.uid.toString(),
//                    firebaseAuth.currentUser?.displayName.toString()),
//                chatRoom.participants,
//                binding.txtEnterMessageGroupChat.text.toString(),
//                R.color.light_gray,
//                chatRoom.uid
//            ))
//                .addOnSuccessListener {
//                    Log.d(ACTIVITY_TAG, "Successfully saved Chat Messages to ChatRoom")
//
//                }.addOnFailureListener {
//                    Log.d(ACTIVITY_TAG,
//                        "Failed saving Chat Messages to ChatRoom: ${it.cause}")
//                }
//
//            binding.txtEnterMessageGroupChat.text.clear()
//
//            saveChatMessage(
//                ChatMessage(
//                    Conversationalist(
//                        firebaseAuth.currentUser?.uid.toString(),
//                        firebaseAuth.currentUser?.displayName.toString()),
//                    arrayListOf(Conversationalist(contact.uid, contact.username)),
//                    binding.txtEnterMessageChat.text.toString(),
//                    R.color.light_gray,
//                    chatRoomUid
//                )
//            ).addOnSuccessListener {
//                Log.d(ACTIVITY_TAG, "Chat Message saved to Firebase Database")
//
//                getCreatedChatMessageData(object : ChatMessageRepository.FirebaseCallback{
//                    override fun onCallback(chatMessage: ChatMessage) {
//
//
////                        chatRoom.messages.add(chatMessage)
//                    }
//                } )
//
//                binding.txtEnterMessageChat.text.clear()
//
//            }
//                .addOnFailureListener {
//                    Log.d(ACTIVITY_TAG,
//                        "Failed saving Chat Message to database: ${it.cause}")
//                }
//
//        }
    }

    private fun onShowMessages() {
//        launch {
//            getAllMessages().addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    val chatMessage = snapshot.getValue(ChatMessage::class.java)
//
//                    if (chatMessage != null) { // todo is message part of this chatroom?
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
//
//
//            getChatRoom(chatRoomUid).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val room = snapshot.getValue(ChatRoom::class.java)
//
//                    if (room != null){
//                        FirebaseUtils.firebaseDatabase.getReference(Constants.REF_CHAT_ROOMS)
//                            .child(room.uid)
//                            .child("big fat doongo")
//                            .setValue(messagesList)
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {}
//            })
//
////            addNewChatRoomMessages()
//
//
//            chatAdapter.setData(messagesList)
//
//            binding.recyclerViewChat.layoutManager = LinearLayoutManager(
//                this@PrivateChatActivity,
//                LinearLayoutManager.VERTICAL,
//                false
//            )
//            binding.recyclerViewChat.setHasFixedSize(true)
//            binding.recyclerViewChat.adapter = chatAdapter
//
//
//        }
    }


    @SuppressLint("RtlHardcoded", "WrongConstant")
    @SuppressWarnings
    private fun showContactAvatarInActionBar(contact: User) {
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