package com.wisekrakr.wisemessenger.components.activity.chat

import android.annotation.SuppressLint
import android.app.ActionBar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.Notification
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.api.repository.ChatMessageRepository.saveChatMessage
import com.wisekrakr.wisemessenger.api.repository.NotificationRepository.saveNotification
import com.wisekrakr.wisemessenger.components.ChatMessageUtils
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.databinding.ActivityGroupChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Actions
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

        onShowMessagesCoroutine()

        binding.btnAddFriendGroupChat.setOnClickListener {
            onAddingFriendToGroup()
        }

        binding.btnSendMessageGroupChat.setOnClickListener {
            onSendMessage()
        }

        //todo this will make this activity search twice for the same messages
        chatMessageAdapter.setLongClickListener(ChatMessageUtils.onChatMessageLongClick(
            this,
            chatRoom,
            binding.txtEnterMessageGroupChat

        ) {
            onShowMessagesOnce()
        })
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
                    if (conversationalist.uid != firebaseAuth.currentUser?.uid) {
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
            EventManager.onAddChatMessageToChatRoom(chatRoom, chatMessage)
        }
    }


     private fun onShowMessagesCoroutine() {
        launch {
            EventManager.onGetAllChatMessagesOfChatRoom(
                chatRoom.uid,
                messagesList
            ) {
                RecyclerViewDataSetup
                    .messages(
                        chatMessageAdapter,
                        it,
                        binding.recyclerViewGroupChat,
                        this@GroupChatActivity
                    )

                binding.recyclerViewGroupChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
            }
        }
    }

    private fun onShowMessagesOnce(){
        messagesList.clear()
        EventManager.onGetAllChatMessagesOfChatRoom(
            chatRoom.uid,
            messagesList
        ) {
            RecyclerViewDataSetup
                .messages(
                    chatMessageAdapter,
                    it,
                    binding.recyclerViewGroupChat,
                    this@GroupChatActivity
                )

            binding.recyclerViewGroupChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
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