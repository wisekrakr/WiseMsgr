package com.wisekrakr.wisemessenger.components.activity.actions

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.HomeActivity
import com.wisekrakr.wisemessenger.api.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.components.EventManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.databinding.ActivityCreateGroupBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import com.wisekrakr.wisemessenger.api.model.ChatRoom
import com.wisekrakr.wisemessenger.api.model.Group
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.model.nondata.Conversationalist
import com.wisekrakr.wisemessenger.api.repository.GroupRepository
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository.getUserProfile
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
    private var selectedParticipants = ArrayList<Conversationalist>()
    private lateinit var iterator: Iterator<Conversationalist>
    private lateinit var createGroupInputsArray: Array<EditText>

    override fun setup() {

        contactsAdapter = ContactsAdapter()

        createGroupInputsArray = arrayOf(binding.etGroupNameAddGroup)

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact())

        selectedParticipants.add(
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

            if (!selectedParticipants.contains(c)) {

                selectedParticipants.add(c)

                makeToast("${c.username} selected")

            } else {
                selectedParticipants.remove(c)

                makeToast("${c.username} deselected")

            }
        }
    }

    override fun supportBar() {
        supportActionBar?.title = "Create new Group"
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
    private fun onCreateGroup(groupName: String) {
        launch {

            if (isNotEmpty(createGroupInputsArray)) {
                val group = Group(groupName)
                val chatRoom = EventManager.onCreateNewChatRoom(
                    selectedParticipants,
                    false
                )
                group.chatRoomUid = chatRoom.uid

                selectedParticipants.forEach { conversationalist->
                    GroupRepository.saveGroup(
                        conversationalist.uid,
                        group
                    ).addOnSuccessListener {
                        makeToast("Successfully created group: $groupName")

                        createNewChatRoomForUserProfile(chatRoom, conversationalist.uid)

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

    /**
     * Every User Profile has a MutableList of chat room uids
     * In this function we update the User Profile by adding the newly made chat room
     * to that mutable list of chat rooms.
     * They consist of private and non-private (group) chat rooms
     * @param chatRoom the newly made chat room with 2 participants
     * @param conversationalistUid user uid
     */
    private fun createNewChatRoomForUserProfile(chatRoom: ChatRoom, conversationalistUid: String){
        launch {
            UserProfileRepository.updateUserWithANewChatRoom(
                chatRoom,
                conversationalistUid
            ).addOnCompleteListener {
                startActivity(Intent(this@CreateGroupActivity, HomeActivity::class.java))
                finish()
            }.addOnFailureListener {
                Log.e(ACTIVITY_TAG, "Failure in user profile chat room creation")
            }
        }
    }

    /**
     * Gets all user profiles from the database and adds them to this activities recycler view
     */
    private fun onShowContacts() {
        launch {
            EventManager.onGetAllContactsOfCurrentUser {
                getContact(it)
            }
        }
    }

    private fun getContact(uid: String) {
        getUserProfile(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue(UserProfile::class.java)

                    if (userProfile?.uid != FirebaseUtils.firebaseAuth.uid) {
                        contacts.add(userProfile!!)
                    }

                    RecyclerViewDataSetup.contacts(
                        contactsAdapter,
                        contacts,
                        binding.recyclerviewAddGroup,
                        this@CreateGroupActivity
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}