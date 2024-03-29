package com.wisekrakr.wisemessenger.components.activity.contact

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.wisekrakr.wisemessenger.api.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.appservice.tasks.ApiManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.databinding.ActivityCreateGroupBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils.firebaseAuth
import com.wisekrakr.wisemessenger.utils.Extensions.isNotEmpty
import com.wisekrakr.wisemessenger.utils.Extensions.isRequired
import com.wisekrakr.wisemessenger.utils.Extensions.makeToast
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Activity for creating a new group or adding to an existing group
 */
class CreateGroupActivity : BaseActivity<ActivityCreateGroupBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityCreateGroupBinding =
        ActivityCreateGroupBinding::inflate

    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts = ArrayList<UserProfile>()
    private var selectedParticipants = ArrayList<Conversationalist>()
    private lateinit var iterator: Iterator<Conversationalist>
    private lateinit var createGroupInputsArray: Array<EditText>

    private lateinit var group: Group
    private lateinit var chatRoom: ChatRoom

    override fun setup() {

        contactsAdapter = ContactsAdapter()

        createGroupInputsArray = arrayOf(binding.etGroupNameAddGroup)

        showContacts()

        //TODO ADDING CONTACT TO EXISTING GROUP

        if (intent.hasExtra("group")) {
            group = intent.getSerializableExtra("group") as Group
            chatRoom = intent.getSerializableExtra("chatRoom") as ChatRoom

            binding.etGroupNameAddGroup.visibility = View.GONE
//            binding.btnCreateAddGroup.text = "Add to group chat"

            binding.btnCreateAddGroup.setOnClickListener {
                addContactToGroup()
                addContactToChatRoom()
            }
        } else {

            selectedParticipants.add(
                Conversationalist(
                    firebaseAuth.currentUser?.uid.toString(),
                    firebaseAuth.currentUser?.displayName.toString()
                )
            )

            binding.btnCreateAddGroup.setOnClickListener {
                createGroup(binding.etGroupNameAddGroup.text.trim().toString())
            }
        }

        contactsAdapter.setClickListener(selectContact())

    }

    private fun selectContact() = object : ContactsAdapter.OnItemClickListener {

        override fun onClick(contact: UserProfile) {

            val c = Conversationalist(contact.uid, contact.username)

            if (!selectedParticipants.contains(c)) {

                selectedParticipants.add(c)

                makeToast("${c.username} selected")

                contactsAdapter.select()

            } else {
                selectedParticipants.remove(c)

                makeToast("${c.username} deselected")

                contactsAdapter.deselect()
            }
        }
    }

    override fun supportBar() {
        if (!intent.hasExtra("group")) {
            supportActionBar?.title = "Create new Group"
        } else {
            supportActionBar?.title = "Add Contact to Group"
        }
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * First check if a group name was typed into the corresponding text view
     * Creates a new Group and new Chat room and adds the chat room uid to the group
     * For all conversationalists we will save the group and if successful, create a new
     * chat room that holds this group. Both will have a reference uid towards each other in the
     * database.
     */
    private fun createGroup(groupName: String) {
        launch {

            if (isNotEmpty(createGroupInputsArray)) {
                val group = Group(groupName)
                val chatRoom = ApiManager.Rooms.onCreateNewChatRoom(
                    selectedParticipants,
                    false
                )
                group.chatRoomUid = chatRoom.uid

                selectedParticipants.forEach { conversationalist ->
                    ApiManager.Groups.onSaveGroup(
                        conversationalist.uid,
                        group,
                        chatRoom,
                        this@CreateGroupActivity
                    )
                }
            } else {
                isRequired(createGroupInputsArray)
            }
        }
    }

    private fun addContactToGroup() {
        launch {
            selectedParticipants.forEach { conversationalist ->
                ApiManager.Groups.onAddContactToGroup(conversationalist, group, chatRoom)
            }
            selectedParticipants.addAll(chatRoom.participants)
        }

    }

    private fun addContactToChatRoom() {
        ApiManager.Rooms.onUpdateChatRoomWithNewContact(chatRoom.uid, selectedParticipants)
    }


    /**
     * Gets all user profiles from the database and adds them to this activities recycler view
     */
    private fun showContacts() {
        launch {
            val list = arrayListOf<String>()
            ApiManager.Profiles.onGetAllContactsOfCurrentUser {
                getContact(it)

                if (intent.hasExtra("chatRoom")) {
                    list.add(it)
                }
            }

            if (list.isNotEmpty()) {
                list.forEach { uid ->
                    chatRoom.participants.forEach { conversationalist ->
                        if (conversationalist.uid != uid) getContact(uid)
                    }
                }
            }
        }
    }

    private fun getContact(uid: String) {

        ApiManager.Profiles.onGetUser(uid){ userProfile ->
            if (userProfile.uid != firebaseAuth.uid) {
                contacts.add(userProfile)
            }

            RecyclerViewDataSetup.contacts(
                contactsAdapter,
                contacts,
                binding.recyclerviewAddGroup,
                this@CreateGroupActivity
            )
        }

    }
}