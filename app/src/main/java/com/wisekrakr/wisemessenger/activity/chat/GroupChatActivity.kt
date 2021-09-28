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
import com.wisekrakr.wisemessenger.databinding.ActivityGroupChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.model.ChatMessage
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository.addMessagesToChatRoom
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

//        showGroupAvatarInActionBar(group)

        chatAdapter = ChatAdapter()

        onShowMessages()

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
            addMessagesToChatRoom(chatRoom, ChatMessage(
                Conversationalist(
                    firebaseAuth.currentUser?.uid.toString(),
                    firebaseAuth.currentUser?.displayName.toString()),
                chatRoom.participants,
                binding.txtEnterMessageGroupChat.text.toString(),
                R.color.light_gray,
                chatRoom.uid
            ))
                .addOnSuccessListener {
                    Log.d(ACTIVITY_TAG, "Successfully saved Chat Messages to ChatRoom")

                }.addOnFailureListener {
                    Log.d(ACTIVITY_TAG,
                        "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                }

            binding.txtEnterMessageGroupChat.text.clear()
        }
    }


    private fun onShowMessages() {
        launch {
            ChatRoomRepository.getChatRoom(group.chatRoomUid)
                .child(Constants.REF_MESSAGES)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val msg = snapshot.getValue(ChatMessage::class.java)

                        if (msg != null) {
                            if (msg.sender?.uid == firebaseAuth.currentUser!!.uid) {
                                msg.messageType = 0
                            } else {
                                msg.messageType = 1
                            }
                            messagesList.add(msg)
                            chatAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?,
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            chatAdapter.setData(messagesList)

            binding.recyclerViewGroupChat.layoutManager = LinearLayoutManager(
                this@GroupChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.recyclerViewGroupChat.setHasFixedSize(true)
            binding.recyclerViewGroupChat.scrollToPosition(messagesList.size)

            binding.recyclerViewGroupChat.adapter = chatAdapter



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