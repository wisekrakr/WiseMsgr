package com.wisekrakr.wisemessenger.components.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.adapter.ChatRoomAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.chat.PrivateChatActivity
import com.wisekrakr.wisemessenger.databinding.FragmentPrivateChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.repository.ChatRoomRepository.getChatRoom
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class PrivateChatFragment : BaseFragment<FragmentPrivateChatBinding>() {

    override val bindingInflater: BindingInflater<FragmentPrivateChatBinding> =
        FragmentPrivateChatBinding::inflate

    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private var conversations: ArrayList<ChatRoom> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRoomAdapter = ChatRoomAdapter()

        onFindAllPrivateConversations()

        chatRoomAdapter.setClickListener(onChatRoomClick)
    }

    companion object {
        const val CHAT_ROOM_KEY = "chat_room"
        const val CONTACT_KEY = "contact"
    }

    private val onChatRoomClick = object : ChatRoomAdapter.OnItemClickListener {
        override fun onClick(chatRoom: ChatRoom) {
            Log.d(FRAGMENT_TAG, "Clicked on chat room ${chatRoom.uid}")

            showChatRoom(chatRoom)
        }
    }

    private fun showChatRoom(chatRoom: ChatRoom) {
        chatRoom.participants.forEach {
            if (it.uid != firebaseAuth.uid) {
                val intent = Intent(requireContext(), PrivateChatActivity::class.java)
                    .putExtra(CHAT_ROOM_KEY, chatRoom)
                    .putExtra(CONTACT_KEY, it)
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun onFindAllPrivateConversations() {

        launch {
            firebaseAuth.uid?.let {
                EventManager.onGetChatRooms(it) { chatRoomId ->
                    getChatRoom(chatRoomId).addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val chatRoom = snapshot.getValue(ChatRoom::class.java)

                                Log.d(FRAGMENT_TAG, chatRoom.toString())

                                if (chatRoom != null) {
                                    if (!conversations.contains(chatRoom)) {
                                        conversations.add(chatRoom)
                                    }
                                }

                                if (isAdded) {
                                    RecyclerViewDataSetup.chatRooms(
                                        chatRoomAdapter,
                                        conversations,
                                        viewBinding.recyclerViewPrivate,
                                        requireContext()
                                    )

                                    viewBinding.tvNumberOfContactsPrivate.text =
                                        conversations.size.toString()
                                }

                                if (chatRoom != null) {
                                    chatRoomAdapter.showChatRoomMessages(chatRoom)
                                }


                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d(FRAGMENT_TAG,
                                    "Error in getting chat room for user ${error.message}")
                            }
                        }
                    )
                }

            }

        }
    }
}