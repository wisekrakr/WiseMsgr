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
import com.wisekrakr.wisemessenger.adapter.ChatAdapter
import com.wisekrakr.wisemessenger.databinding.ActivityGroupChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.components.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatMessageRepository.saveChatMessage
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.addMessagesToChatRoom
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Constants
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class GroupChatActivity : BaseActivity<ActivityGroupChatBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityGroupChatBinding =
        ActivityGroupChatBinding::inflate

    private lateinit var group: Group
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRoom: ChatRoom

    private val messagesList: ArrayList<ChatMessage> = ArrayList()


    override fun setup() {
        group = intent.getSerializableExtra(GroupsFragment.GROUP_KEY) as Group
        chatRoom = intent.getSerializableExtra(GroupsFragment.CHAT_ROOM_KEY) as ChatRoom

//        showGroupAvatarInActionBar(group)

        chatAdapter = ChatAdapter()

        onShowMessages(chatRoom.uid)

        binding.btnAddFriendGroupChat.setOnClickListener {
            onAddingFriendToGroup()
        }

        binding.btnSendMessageGroupChat.setOnClickListener {
            onSendMessage()
        }
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
            addMessagesToChatRoom(chatRoom, chatMessage)
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
                            chatAdapter.notifyDataSetChanged()
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

            chatAdapter.setData(messagesList)

            val lm = LinearLayoutManager(
                this@GroupChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            lm.stackFromEnd = true
            binding.recyclerViewGroupChat.layoutManager = lm
            binding.recyclerViewGroupChat.setHasFixedSize(true)

            binding.recyclerViewGroupChat.adapter = chatAdapter

            binding.recyclerViewGroupChat.scrollToPosition(chatAdapter.itemCount-1)

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