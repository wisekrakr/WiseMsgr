package com.wisekrakr.wisemessenger.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.activity.chat.PrivateChatActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.databinding.FragmentContactsBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.repository.UserRepository
import com.wisekrakr.wisemessenger.utils.Extensions.FRAGMENT_TAG
import kotlinx.coroutines.launch


class ContactsFragment : BaseFragment<FragmentContactsBinding>() {

    override val bindingInflater: BindingInflater<FragmentContactsBinding> = FragmentContactsBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var arrayContacts = ArrayList<User>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsAdapter = ContactsAdapter(R.layout.contact_item)

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)
    }

    companion object {
        const val CONTACT_KEY = "contact"
        const val CHAT_ROOM_KEY = "chat_room"
    }

    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: User) {
            Log.d(FRAGMENT_TAG, "Clicked on: ${contact.username} ")

//            findChatRoomUid(contact)
            startChatting(contact)
//            launch {
//                if (duplicateChatRoomUid.isNullOrEmpty()) {
//                    ChatRoomRepository.getChatRoom(duplicateChatRoomUid!!).addListenerForSingleValueEvent(object :
//                        ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val chatRoom = snapshot.getValue(ChatRoom::class.java)
//
//                            if (chatRoom != null) {
//                                startChatting(contact)
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.e(FRAGMENT_TAG, error.message)
//                        }
//                    })
//                } else {
//                    onCreateNewChatRoom(contact)
//                }
//            }
        }
    }

    private fun onCreateNewChatRoom(contact: User) {
//        ChatRoomRepository.createChatRoom(
//            ChatRoom(
//                arrayListOf(
//                    FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
//                    contact.uid
//                )
//            )
//        ).addOnSuccessListener {
//            startChatting(contact)
//            Log.d(ACTIVITY_TAG, "Created new chat room")
//
//        }.addOnFailureListener {
//            Log.d(ACTIVITY_TAG, "Failed to create new chat room ${it.cause}")
//        }
    }


    private fun startChatting(contact: User) {
        val intent = Intent(requireContext(), PrivateChatActivity::class.java)
            .putExtra(CONTACT_KEY, contact)
        startActivity(intent)
        activity?.finish()
    }

    private fun onShowContacts() {
        launch {
            this.let {
                UserRepository.getUsers()
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
//                            Log.d(FRAGMENT_TAG, it.toString())
                            val user = it.getValue(User::class.java)!!

                            if (user.uid != FirebaseUtils.firebaseAuth.uid) {
                                arrayContacts.add(it.getValue(User::class.java)!!)
                            }
                        }

                        contactsAdapter.setData(arrayContacts)

                        viewBinding.tvNumberOfContactsContacts.text = arrayContacts.size.toString()

                        viewBinding.recyclerViewContacts.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        viewBinding.recyclerViewContacts.setHasFixedSize(true)
                        viewBinding.recyclerViewContacts.adapter = contactsAdapter


                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(FRAGMENT_TAG, error.message)
                    }
                })

                Log.d(FRAGMENT_TAG, "Showing contacts.... ")
            }
        }
    }
}