package com.wisekrakr.wisemessenger.components.activity.chat

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.contact.CreateGroupActivity
import com.wisekrakr.wisemessenger.components.fragments.GroupsFragment
import com.wisekrakr.wisemessenger.components.utils.ChatMessageUtils
import com.wisekrakr.wisemessenger.databinding.ActivityGroupChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class GroupChatActivity : BaseActivity<ActivityGroupChatBinding>(), ChatActivityMethods {
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
            val intent = Intent(this, CreateGroupActivity::class.java)
            intent.putExtra("group", group)
            intent.putExtra("chatRoom", chatRoom)
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)

            startActivity(intent)
        }

        binding.btnSendMessageGroupChat.setOnClickListener {
            onSendMessage()
        }

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

            if (!binding.txtEnterMessageGroupChat.text.isNullOrEmpty()) {

                ApiManager.Messages.onSaveChatMessage(
                    ChatMessage(
                        Conversationalist(
                            firebaseAuth.currentUser?.uid.toString(),
                            firebaseAuth.currentUser?.displayName.toString()),
                        chatRoom.participants,
                        binding.txtEnterMessageGroupChat.text.toString(),
                        R.color.light_gray,
                        chatRoom.uid
                    ),
                    chatRoom.uid
                ){
                    binding.txtEnterMessageGroupChat.setText("")
                }
            } else {
                makeToast("You cannot send empty messages.")
            }
        }
    }


    override fun onShowMessagesCoroutine() {
        launch {
            ApiManager.Rooms.onGetAllChatMessagesOfChatRoom(
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

    override fun onShowMessagesOnce() {
        messagesList.clear()
        ApiManager.Rooms.onGetAllChatMessagesOfChatRoom(
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