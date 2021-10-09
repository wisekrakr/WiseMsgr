package com.wisekrakr.wisemessenger.components.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.adapter.ChatRoomAdapter
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.databinding.FragmentPrivateChatBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
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

    }

    //todo find all chat rooms for this user and display in recyclerview
    private fun onFindAllPrivateConversations() {

        launch {
            EventManager.onGetChatRooms(firebaseAuth.uid!!) {

                ChatRoomRepository.getChatRoom(it).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatRoom = snapshot.getValue(ChatRoom::class.java)

                            if (chatRoom != null)
                                conversations.add(chatRoom)

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d(FRAGMENT_TAG,
                                "Error in getting chat room for user ${error.message}")
                        }
                    }
                )

                RecyclerViewDataSetup.chatRooms(
                    chatRoomAdapter,
                    conversations,
                    viewBinding.recyclerViewPrivate,
                    requireContext()
                )

                viewBinding.tvNumberOfContactsPrivate.text = conversations.size.toString()

            }
        }
    }

}