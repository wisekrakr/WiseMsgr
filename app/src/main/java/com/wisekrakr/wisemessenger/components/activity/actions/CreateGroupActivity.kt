package com.wisekrakr.wisemessenger.components.activity.actions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.databinding.ActivityCreateGroupBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.model.ChatRoom
import com.wisekrakr.wisemessenger.model.Group
import com.wisekrakr.wisemessenger.model.UserProfile
import com.wisekrakr.wisemessenger.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.repository.ChatRoomRepository
import com.wisekrakr.wisemessenger.repository.GroupRepository
import com.wisekrakr.wisemessenger.utils.Extensions.ACTIVITY_TAG
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.isRequired
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch

class CreateGroupActivity : BaseActivity<ActivityCreateGroupBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityCreateGroupBinding =
        ActivityCreateGroupBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts = ArrayList<UserProfile>()
    private var selectedContacts = ArrayList<Conversationalist>()
    private lateinit var iterator: Iterator<Conversationalist>
    private lateinit var chatRoom: ChatRoom
    private lateinit var createGroupInputsArray: Array<EditText>

    override fun setup() {

        contactsAdapter = ContactsAdapter()

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

        override fun onClick(contact: UserProfile) {

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
                group.chatRoomUid = EventManager.onCreateNewChatRoom(
                    selectedContacts,
                    false
                ).uid

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
            EventManager.onGetAllUsers(contacts) {
                RecyclerViewDataSetup.contacts(
                    contactsAdapter,
                    contacts,
                    binding.recyclerviewAddGroup,
                    this@CreateGroupActivity
                )
            }
        }
    }


}