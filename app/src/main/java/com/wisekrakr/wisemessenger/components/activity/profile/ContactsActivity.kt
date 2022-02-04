package com.wisekrakr.wisemessenger.components.activity.profile

import android.content.Intent
import android.view.LayoutInflater
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wisekrakr.wisemessenger.api.adapter.ContactsAdapter
import com.wisekrakr.wisemessenger.api.model.UserProfile
import com.wisekrakr.wisemessenger.api.repository.UserProfileRepository
import com.wisekrakr.wisemessenger.appservice.tasks.TaskManager
import com.wisekrakr.wisemessenger.components.RecyclerViewDataSetup
import com.wisekrakr.wisemessenger.components.activity.BaseActivity
import com.wisekrakr.wisemessenger.components.activity.actions.SearchActivity
import com.wisekrakr.wisemessenger.databinding.ActivityContactsBinding
import com.wisekrakr.wisemessenger.firebase.FirebaseUtils
import kotlinx.coroutines.launch

/**
 * Shows a list of the contacts in the user list.
 * This activity could also start via GroupChatActivity if a new contact is wished to be added:
 * if this is the case, this Activity has the extra's of group and chatRoom to add to contact's data
 */
class ContactsActivity : BaseActivity<ActivityContactsBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityContactsBinding =
        ActivityContactsBinding::inflate


    private lateinit var contactsAdapter: ContactsAdapter
    private var contacts = mutableSetOf<UserProfile>()

    override fun setup() {
        contactsAdapter = ContactsAdapter()

        onShowContacts()

        contactsAdapter.setClickListener(onSelectContact)

    }

    private val onSelectContact = object : ContactsAdapter.OnItemClickListener {
        override fun onClick(contact: UserProfile) {
            showProfile(contact)
        }
    }

    private fun showProfile(userProfile: UserProfile) {
        val intent = Intent(this, ProfileActivity::class.java)
            .putExtra(SearchActivity.USER_PROFILE_KEY, userProfile)
        startActivity(intent)
        finish()
    }

    private fun onShowContacts() {
        launch {
            TaskManager.Profiles.onGetAllContactsOfCurrentUser {
                getContact(it)
            }
        }
    }

    private fun getContact(uid: String) {
        UserProfileRepository.getUserProfile(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userProfile = snapshot.getValue(UserProfile::class.java)

                    if (userProfile?.uid != FirebaseUtils.firebaseAuth.uid) {
                        contacts.add(userProfile!!)
                    }

                    RecyclerViewDataSetup
                        .contacts(
                            contactsAdapter,
                            contacts.toList(),
                            binding.recyclerViewContacts,
                            this@ContactsActivity
                        )

                    binding.tvNumberOfContactsContacts.text = contacts.size.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun supportBar() {
        supportActionBar?.title = "My Contacts"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}