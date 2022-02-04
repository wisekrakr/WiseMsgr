package com.wisekrakr.wisemessenger.components.activity.chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.adapter.ChatMessageAdapter
import com.wisekrakr.wisemessenger.api.model.ChatMessage
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.repository.ChatMessageRepository.saveChatMessage
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.addMessageToChatRoom
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfile
import com.wisekrakr.wisemessenger.appservice.tasks.TaskManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.fragments.PrivateChatFragment.Companion.CHAT_ROOM_KEY
import com.wisekrakr.wisemessenger.components.fragments.PrivateChatFragment.Companion.CONTACT_KEY
import com.wisekrakr.wisemessenger.components.utils.ChatMessageUtils
import com.wisekrakr.wisemessenger.databinding.ActivityPrivateChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Actions
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class PrivateChatActivity : BaseActivity<ActivityPrivateChatBinding>(), ChatActivityMethods {

    private lateinit var contact: Conversationalist
    private lateinit var userProfile: UserProfile
    private lateinit var chatRoom: ChatRoom
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var chatActivityMethodsImpl: ChatActivityMethodsImpl

    private val messagesList: ArrayList<ChatMessage> = ArrayList()

    override val bindingInflater: (LayoutInflater) -> ActivityPrivateChatBinding =
        ActivityPrivateChatBinding::inflate

    override fun setup() {

        contact = intent.getSerializableExtra(CONTACT_KEY) as Conversationalist
        chatRoom = intent.getSerializableExtra(CHAT_ROOM_KEY) as ChatRoom

        Log.d(ACTIVITY_TAG, "Chatting with: ${contact.username} in chat room: ${chatRoom.uid}")

        getCurrentContact()

        chatMessageAdapter = ChatMessageAdapter()
//        chatActivityMethodsImpl = ChatActivityMethodsImpl()

        onShowMessagesCoroutine()

        //TODO WORKS ONLY WITH BUTTON, FIGURE IMAGEBUTTON OUT DAMNIT
        binding.btnSendMessagePrivateChat.setOnClickListener {

            onSendMessage()

            println("TEST TEST TEST TEST")
        }

        chatMessageAdapter.setLongClickListener(ChatMessageUtils.onChatMessageLongClick(
            this,
            chatRoom,
            binding.txtEnterMessagePrivateChat

        ) {
            onShowMessagesOnce()
        })
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
                    addMessageToChatRoom(chatRoom.uid, chatMessage)
                        .addOnSuccessListener {
                            Log.d(ACTIVITY_TAG, "Successfully saved Chat Messages to ChatRoom")

                            binding.txtEnterMessagePrivateChat.text.clear()

//                            chatRoom.participants.forEach { conversationalist ->
//                                if(conversationalist.uid != firebaseAuth.currentUser?.uid){
//                                    saveNotification(
//                                        Notification(
//                                            conversationalist.uid,
//                                            conversationalist.username,
//                                            firebaseAuth.currentUser?.uid.toString(),
//                                            firebaseAuth.currentUser?.displayName.toString(),
//                                            chatMessage.message,
//                                            NotificationType.MESSAGE
//                                        )
//                                    )
//                                }
//                            }
                        }.addOnFailureListener {
                            Log.d(ACTIVITY_TAG,
                                "Failed saving Chat Messages to ChatRoom: ${it.cause}")
                        }

                }
                    .addOnFailureListener {
                        Log.d(ACTIVITY_TAG,
                            "Failed saving Chat Message to database: ${it.cause}")
                    }
            } else {
                makeToast("You cannot send empty messages.")
            }
        }
    }
//    private fun onSendMessage() {
//        launch {
//
//            if (!binding.txtEnterMessagePrivateChat.text.isNullOrEmpty()) {
//
//                chatActivityMethodsImpl.saveMessage(
//                    ChatMessage(
//                        Conversationalist(
//                            firebaseAuth.currentUser?.uid.toString(),
//                            firebaseAuth.currentUser?.displayName.toString()),
//                        chatRoom.participants,
//                        binding.txtEnterMessagePrivateChat.text.toString(),
//                        R.color.light_gray,
//                        chatRoom.uid
//                    ),
//                    chatRoom,
//                    binding.txtEnterMessagePrivateChat.text
//                )
//
//            } else {
//                makeToast("You cannot send empty messages.")
//            }
//        }
//    }

    override fun onShowMessagesCoroutine() {
        launch {
            TaskManager.Rooms.onGetAllChatMessagesOfChatRoom(
                chatRoom.uid,
                messagesList
            ) {
                RecyclerViewDataSetup
                    .messages(
                        chatMessageAdapter,
                        it,
                        binding.recyclerViewPrivateChat,
                        this@PrivateChatActivity
                    )

                binding.recyclerViewPrivateChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
            }
        }
    }

    override fun onShowMessagesOnce() {
        messagesList.clear()
        TaskManager.Rooms.onGetAllChatMessagesOfChatRoom(
            chatRoom.uid,
            messagesList
        ) {
            RecyclerViewDataSetup
                .messages(
                    chatMessageAdapter,
                    it,
                    binding.recyclerViewPrivateChat,
                    this@PrivateChatActivity
                )

            binding.recyclerViewPrivateChat.smoothScrollToPosition(chatMessageAdapter.itemCount)
        }
    }

    @SuppressLint("ResourceType")
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
            ActionBar.LayoutParams.WRAP_CONTENT,
            Gravity.START
        )

        circleImageView.layoutParams = layoutParams
        supportActionBar?.customView = circleImageView
    }

}