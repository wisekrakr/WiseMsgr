package com.wisekrakr.wisemessenger.components.activity.chat

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
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.api.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.databinding.ActivityGroupChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.components.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.Notification
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.api.repository.ChatMessageRepository.saveChatMessage
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.api.repository.NotificationRepository.saveNotification
import com.wisekrakr.wisemessenger.components.ChatMessageUtils
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Constants
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class GroupChatActivity : BaseActivity<ActivityGroupChatBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityGroupChatBinding =
        ActivityGroupChatBinding::inflate

    private lateinit var group: Group
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var chatRoom: ChatRoom

    private val messagesList: ArrayList<ChatMessage> = ArrayList()


    override fun setup() {
        group = intent.getSerializableExtra(GroupsFragment.GROUP_KEY) as Group
        chatRoom = intent.getSerializableExtra(GroupsFragment.CHAT_ROOM_KEY) as ChatRoom

//        showGroupAvatarInActionBar(group)

        chatMessageAdapter = ChatMessageAdapter()

        onShowMessages(chatRoom.uid)

        binding.btnAddFriendGroupChat.setOnClickListener {
            onAddingFriendToGroup()
        }

        binding.btnSendMessageGroupChat.setOnClickListener {
            onSendMessage()
        }

        chatMessageAdapter.setLongClickListener(ChatMessageUtils.onChatMessageLongClick(
            this,
            chatRoom,
            binding.txtEnterMessageGroupChat,
            onShowMessages(chatRoom.uid)
        ))
    }

    override fun supportBar() {
        supportActionBar?.title = group.groupName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Creates a new ChatMessage and saves it in the Firebase Database
     * On Success -> add to ChatRoom messages array for all chat room users
     */
    private fun onSendMessage() {
        launch {
            val chatMessage = ChatMessage(
                Conversationalist(
                    firebaseAuth.currentUser?.uid.toString(),
                    firebaseAuth.currentUser?.displayName.toString()),
                chatRoom.participants,
                binding.txtEnterMessageGroupChat.text.toString(),
                R.color.light_gray,
                group.chatRoomUid
            )

            saveChatMessage(
                chatMessage
            ).addOnSuccessListener {
                Log.d(ACTIVITY_TAG, "Chat Message saved to Firebase Database")

                addChatMessageToChatRoom(chatMessage)
                binding.txtEnterMessageGroupChat.text.clear()

                chatRoom.participants.forEach { conversationalist ->
                    if(conversationalist.uid != firebaseAuth.currentUser?.uid){
                        saveNotification(
                            Notification(
                                conversationalist.uid,
                                conversationalist.username,
                                firebaseAuth.currentUser?.uid.toString(),
                                firebaseAuth.currentUser?.displayName.toString(),
                                "New Message in group: ${chatRoom.uid}",
                                NotificationType.MESSAGE
                            )
                        )
                    }

                }

            }
                .addOnFailureListener {
                    Log.d(ACTIVITY_TAG,
                        "Failed saving Chat Message to database: ${it.cause}")
                }
        }
    }

    /**
     * Add a ChatMessage uid in the messages child for this ChatRoom Database Object
     */
    private fun addChatMessageToChatRoom(chatMessage: ChatMessage) {
        launch {
            ChatRoomRepository.addMessageToChatRoom(chatRoom, chatMessage)
                .addOnSuccessListener {
                    Log.d(ACTIVITY_TAG, "Successfully saved Chat Messages to ChatRoom")

                }.addOnFailureListener {
                    Log.d(ACTIVITY_TAG,
                        "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                }

        }
    }

    /**
     * Search Firebase Database for ChatRoom with ChatMessage Uid child.
     * Then start onGetChatMessages function to search database for corresponding ChatMessages.
     */
    private fun onShowMessages(uid: String) {
        launch {
            getChatRoom(uid)
                .child(Constants.REF_MESSAGES)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        val message = snapshot.getValue(ChatMessage::class.java)
                        if (message != null) {
                            if (message.sender?.uid == firebaseAuth.currentUser!!.uid) {
                                message.messageType = 0
                            } else {
                                message.messageType = 1
                            }
                            messagesList.add(message)
                            chatMessageAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })

            chatMessageAdapter.setData(messagesList)

            val lm = LinearLayoutManager(
                this@GroupChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            lm.stackFromEnd = true
            binding.recyclerViewGroupChat.layoutManager = lm
            binding.recyclerViewGroupChat.setHasFixedSize(true)

            binding.recyclerViewGroupChat.adapter = chatMessageAdapter

            binding.recyclerViewGroupChat.scrollToPosition(chatMessageAdapter.itemCount-1)

        }


    }


    private fun onAddingFriendToGroup() {
//        getGroup(
//            firebaseAuth.currentUser!!.uid,
//
//        )
    }


    @SuppressLint("RtlHardcoded", "WrongConstant")
    @SuppressWarnings
    private fun showGroupAvatarInActionBar(group: Group) {
        supportActionBar?.displayOptions =
            supportActionBar!!.displayOptions or ActionBar.DISPLAY_SHOW_CUSTOM

        val circleImageView = CircleImageView(supportActionBar!!.themedContext)
        circleImageView.scaleType = ImageView.ScaleType.CENTER_CROP

        Actions.ImageActions.loadImage(
            group.avatarUrl,
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