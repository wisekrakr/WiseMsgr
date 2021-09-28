package com.wisekrakr.wisemessenger.activity.actions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.activity.BaseActivity
import com.wisekrakr.wisemessenger.activity.HomeActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.databinding.ActivityCreateGroupBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.User
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.repository.GroupRepository
import com.wisekrakr.wisemessenger.repository.UserRepository
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.isRequired
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import com.wisekrakr.wisemessenger.utils.Extensions.notification
import kotlinx.coroutines.launch

class CreateGroupActivity : BaseActivity<ActivityCreateGroupBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityCreateGroupBinding =
        ActivityCreateGroupBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var arrayContacts = ArrayList<User>()
    private var selectedContacts = ArrayList<Conversationalist>()
    private lateinit var iterator: Iterator<Conversationalist>
    private lateinit var chatRoom: ChatRoom
    private lateinit var createGroupInputsArray: Array<EditText>

    override fun setup() {

        contactsAdapter = ContactsAdapter(R.layout.contact_item_select)

        createGroupInputsArray = arrayOf(binding.etGroupNameAddGroup)

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact())

        selectedContacts.add(
            Conversationalist(
                FirebaseUtils.firebaseAuth.currentUser?.uid.toString(),
                FirebaseUtils.firebaseAuth.currentUser?.displayName.toString()
            )
        )

        binding.btnCreateAddGroup.setOnClickListener {
            onCreateGroup(binding.etGroupNameAddGroup.text.trim().toString())
        }
    }

    private fun onSelectContact() = object : ContactsAdapter.OnItemClickListener {

        override fun onClick(contact: User) {

            val c = Conversationalist(contact.uid, contact.username)

            if (!selectedContacts.contains(c)) {

                selectedContacts.add(c)

                makeToast("${c.username} selected")

            } else {
                selectedContacts.remove(c)

                makeToast("${c.username} deselected")

            }
        }
    }

    override fun supportBar() {
        supportActionBar?.title = "Create new Group"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun onCreateGroup(groupName: String) {
        launch {

            if (isNotEmpty(createGroupInputsArray)) {
                val group = Group(groupName)
                group.chatRoomUid = onCreateNewChatRoom().uid

                selectedContacts.forEach {
                    GroupRepository.saveGroup(
                        it.uid,
                        group
                    ).addOnSuccessListener {
                        makeToast("Successfully created group: $groupName")

                        startActivity(Intent(this@CreateGroupActivity, HomeActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        makeToast("Failed to create group: $groupName")
                        Log.e(ACTIVITY_TAG, "Failure in group creation")
                    }
                }


            } else {
                isRequired(createGroupInputsArray)
            }

        }
    }

    private fun onShowContacts() {
        launch {
            this.let {
                UserRepository.getUsers()
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val user = it.getValue(User::class.java)!!

                                if (user.uid != FirebaseUtils.firebaseAuth.uid) {
                                    arrayContacts.add(it.getValue(User::class.java)!!)
                                }
                            }

                            contactsAdapter.setData(arrayContacts)

                            binding.recyclerviewAddGroup.layoutManager = LinearLayoutManager(
                                this@CreateGroupActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            binding.recyclerviewAddGroup.setHasFixedSize(true)
                            binding.recyclerviewAddGroup.adapter = contactsAdapter
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(ACTIVITY_TAG, error.message)
                        }
                    })

                Log.d(ACTIVITY_TAG, "Showing contacts.... ")
            }
        }
    }

    /**
     * Creates a new ChatRoom data object and returns it to be used by a new group
     */
    private fun onCreateNewChatRoom(): ChatRoom {

        chatRoom = ChatRoom(selectedContacts)

        ChatRoomRepository.createChatRoom(
            chatRoom
        ).addOnSuccessListener {
            Log.d(ACTIVITY_TAG, "Created new chat room")
        }.addOnFailureListener {
            Log.d(ACTIVITY_TAG, "Failed to create new chat room ${it.cause}")
            return@addOnFailureListener
        }

        return chatRoom
    }
}